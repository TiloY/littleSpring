package org.littlespring.utils;


import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentReferenceHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75F;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    private static final ReferenceType DEFAULT_REFERENCE_TYPE;
    private static final int MAXIMUM_CONCURRENCY_LEVEL = 65536;
    private static final int MAXIMUM_SEGMENT_SIZE = 1073741824;
    private final Segment[] segments;
    private final float loadFactor;
    private final ReferenceType referenceType;
    private final int shift;
    private Set<Map.Entry<K, V>> entrySet;

    public ConcurrentReferenceHashMap() {
        this(16, 0.75F, 16, DEFAULT_REFERENCE_TYPE);
    }

    public ConcurrentReferenceHashMap(int initialCapacity) {
        this(initialCapacity, 0.75F, 16, DEFAULT_REFERENCE_TYPE);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, 16, DEFAULT_REFERENCE_TYPE);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, int concurrencyLevel) {
        this(initialCapacity, 0.75F, concurrencyLevel, DEFAULT_REFERENCE_TYPE);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, ReferenceType referenceType) {
        this(initialCapacity, 0.75F, 16, referenceType);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        this(initialCapacity, loadFactor, concurrencyLevel, DEFAULT_REFERENCE_TYPE);
    }

    public ConcurrentReferenceHashMap(int initialCapacity, float loadFactor, int concurrencyLevel, ReferenceType referenceType) {
        Assert.isTrue(initialCapacity >= 0, "Initial capacity must not be negative");
        Assert.isTrue(loadFactor > 0.0F, "Load factor must be positive");
        Assert.isTrue(concurrencyLevel > 0, "Concurrency level must be positive");
        Assert.notNull(referenceType, "Reference type must not be null");
        this.loadFactor = loadFactor;
        this.shift = calculateShift(concurrencyLevel, 65536);
        int size = 1 << this.shift;
        this.referenceType = referenceType;
        int roundedUpSegmentCapacity = (int)(((long)(initialCapacity + size) - 1L) / (long)size);
        this.segments = (Segment[])((Segment[]) Array.newInstance(Segment.class, size));

        for(int i = 0; i < this.segments.length; ++i) {
            this.segments[i] = new Segment(roundedUpSegmentCapacity);
        }

    }

    protected final float getLoadFactor() {
        return this.loadFactor;
    }

    protected final int getSegmentsSize() {
        return this.segments.length;
    }

    protected final Segment getSegment(int index) {
        return this.segments[index];
    }

    protected ReferenceManager createReferenceManager() {
        return new ReferenceManager();
    }

    protected int getHash(Object o) {
        int hash = o == null ? 0 : o.hashCode();
        hash += hash << 15 ^ -12931;
        hash ^= hash >>> 10;
        hash += hash << 3;
        hash ^= hash >>> 6;
        hash += (hash << 2) + (hash << 14);
        hash ^= hash >>> 16;
        return hash;
    }

    public V get(Object key) {
        Reference<K, V> reference = this.getReference(key, Restructure.WHEN_NECESSARY);
        Entry<K, V> entry = reference != null ? reference.get() : null;
        return entry != null ? entry.getValue() : null;
    }

    public boolean containsKey(Object key) {
        Reference<K, V> reference = this.getReference(key, Restructure.WHEN_NECESSARY);
        Entry<K, V> entry = reference != null ? reference.get() : null;
        return entry != null && ObjectUtils.nullSafeEquals(entry.getKey(), key);
    }

    protected final Reference<K, V> getReference(Object key, Restructure restructure) {
        int hash = this.getHash(key);
        return this.getSegmentForHash(hash).getReference(key, hash, restructure);
    }

    public V put(K key, V value) {
        return this.put(key, value, true);
    }

    public V putIfAbsent(K key, V value) {
        return this.put(key, value, false);
    }

    private V put(K key, final V value, final boolean overwriteExisting) {
        return this.doTask(key, new Task<V>(new TaskOption[]{TaskOption.RESTRUCTURE_BEFORE, TaskOption.RESIZE}) {
            protected V execute(Reference<K, V> reference, Entry<K, V> entry, Entries entries) {
                if (entry != null) {
                    V previousValue = entry.getValue();
                    if (overwriteExisting) {
                        entry.setValue(value);
                    }

                    return previousValue;
                } else {
                    entries.add(value);
                    return null;
                }
            }
        });
    }

    public V remove(Object key) {
        return this.doTask(key, new Task<V>(new TaskOption[]{TaskOption.RESTRUCTURE_AFTER, TaskOption.SKIP_IF_EMPTY}) {
            protected V execute(Reference<K, V> reference, Entry<K, V> entry) {
                if (entry != null) {
                    reference.release();
                    return entry.value;
                } else {
                    return null;
                }
            }
        });
    }

    public boolean remove(Object key, final Object value) {
        return (Boolean)this.doTask(key, new Task<Boolean>(new TaskOption[]{TaskOption.RESTRUCTURE_AFTER, TaskOption.SKIP_IF_EMPTY}) {
            protected Boolean execute(Reference<K, V> reference, Entry<K, V> entry) {
                if (entry != null && ObjectUtils.nullSafeEquals(entry.getValue(), value)) {
                    reference.release();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public boolean replace(K key, final V oldValue, final V newValue) {
        return (Boolean)this.doTask(key, new Task<Boolean>(new TaskOption[]{TaskOption.RESTRUCTURE_BEFORE, TaskOption.SKIP_IF_EMPTY}) {
            protected Boolean execute(Reference<K, V> reference, Entry<K, V> entry) {
                if (entry != null && ObjectUtils.nullSafeEquals(entry.getValue(), oldValue)) {
                    entry.setValue(newValue);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    public V replace(K key, final V value) {
        return this.doTask(key, new Task<V>(new TaskOption[]{TaskOption.RESTRUCTURE_BEFORE, TaskOption.SKIP_IF_EMPTY}) {
            protected V execute(Reference<K, V> reference, Entry<K, V> entry) {
                if (entry != null) {
                    V previousValue = entry.getValue();
                    entry.setValue(value);
                    return previousValue;
                } else {
                    return null;
                }
            }
        });
    }

    public void clear() {
        Segment[] var1 = this.segments;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Segment segment = var1[var3];
            segment.clear();
        }

    }

    public void purgeUnreferencedEntries() {
        Segment[] var1 = this.segments;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Segment segment = var1[var3];
            segment.restructureIfNecessary(false);
        }

    }

    public int size() {
        int size = 0;
        Segment[] var2 = this.segments;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Segment segment = var2[var4];
            size += segment.getCount();
        }

        return size;
    }

    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet();
        }

        return this.entrySet;
    }

    private <T> T doTask(Object key, Task<T> task) {
        int hash = this.getHash(key);
        return this.getSegmentForHash(hash).doTask(hash, key, task);
    }

    private Segment getSegmentForHash(int hash) {
        return this.segments[hash >>> 32 - this.shift & this.segments.length - 1];
    }

    protected static int calculateShift(int minimumValue, int maximumValue) {
        int shift = 0;

        for(int value = 1; value < minimumValue && value < maximumValue; ++shift) {
            value <<= 1;
        }

        return shift;
    }

    static {
        DEFAULT_REFERENCE_TYPE = ReferenceType.SOFT;
    }

    private static final class WeakEntryReference<K, V> extends WeakReference<Entry<K, V>> implements Reference<K, V> {
        private final int hash;
        private final Reference<K, V> nextReference;

        public WeakEntryReference(Entry<K, V> entry, int hash, Reference<K, V> next, ReferenceQueue<Entry<K, V>> queue) {
            super(entry, queue);
            this.hash = hash;
            this.nextReference = next;
        }

        public int getHash() {
            return this.hash;
        }

        public Reference<K, V> getNext() {
            return this.nextReference;
        }

        public void release() {
            this.enqueue();
            this.clear();
        }
    }

    private static final class SoftEntryReference<K, V> extends SoftReference<Entry<K, V>> implements Reference<K, V> {
        private final int hash;
        private final Reference<K, V> nextReference;

        public SoftEntryReference(Entry<K, V> entry, int hash, Reference<K, V> next, ReferenceQueue<Entry<K, V>> queue) {
            super(entry, queue);
            this.hash = hash;
            this.nextReference = next;
        }

        public int getHash() {
            return this.hash;
        }

        public Reference<K, V> getNext() {
            return this.nextReference;
        }

        public void release() {
            this.enqueue();
            this.clear();
        }
    }

    protected class ReferenceManager {
        private final ReferenceQueue<Entry<K, V>> queue = new ReferenceQueue();

        protected ReferenceManager() {
        }

        public Reference<K, V> createReference(Entry<K, V> entry, int hash, Reference<K, V> next) {
            return (Reference)(ConcurrentReferenceHashMap.this.referenceType == ReferenceType.WEAK ? new WeakEntryReference(entry, hash, next, this.queue) : new SoftEntryReference(entry, hash, next, this.queue));
        }

        public Reference<K, V> pollForPurge() {
            return (Reference)this.queue.poll();
        }
    }

    protected static enum Restructure {
        WHEN_NECESSARY,
        NEVER;

        private Restructure() {
        }
    }

    private class EntryIterator implements Iterator<Map.Entry<K, V>> {
        private int segmentIndex;
        private int referenceIndex;
        private Reference<K, V>[] references;
        private Reference<K, V> reference;
        private Entry<K, V> next;
        private Entry<K, V> last;

        public EntryIterator() {
            this.moveToNextSegment();
        }

        public boolean hasNext() {
            this.getNextIfNecessary();
            return this.next != null;
        }

        public Entry<K, V> next() {
            this.getNextIfNecessary();
            if (this.next == null) {
                throw new NoSuchElementException();
            } else {
                this.last = this.next;
                this.next = null;
                return this.last;
            }
        }

        private void getNextIfNecessary() {
            while(this.next == null) {
                this.moveToNextReference();
                if (this.reference == null) {
                    return;
                }

                this.next = this.reference.get();
            }

        }

        private void moveToNextReference() {
            if (this.reference != null) {
                this.reference = this.reference.getNext();
            }

            while(this.reference == null && this.references != null) {
                if (this.referenceIndex >= this.references.length) {
                    this.moveToNextSegment();
                    this.referenceIndex = 0;
                } else {
                    this.reference = this.references[this.referenceIndex];
                    ++this.referenceIndex;
                }
            }

        }

        private void moveToNextSegment() {
            this.reference = null;
            this.references = null;
            if (this.segmentIndex < ConcurrentReferenceHashMap.this.segments.length) {
                this.references = ConcurrentReferenceHashMap.this.segments[this.segmentIndex].references;
                ++this.segmentIndex;
            }

        }

        public void remove() {
            Assert.state(this.last != null, "No element to remove");
            ConcurrentReferenceHashMap.this.remove(this.last.getKey());
        }
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        private EntrySet() {
        }

        public Iterator<Map.Entry<K, V>> iterator() {
            return ConcurrentReferenceHashMap.this.new EntryIterator();
        }

        public boolean contains(Object o) {
            if (o != null && o instanceof Map.Entry) {
                Map.Entry<?, ?> entry = (Map.Entry)o;
                Reference<K, V> reference = ConcurrentReferenceHashMap.this.getReference(entry.getKey(), Restructure.NEVER);
                Entry<K, V> other = reference != null ? reference.get() : null;
                if (other != null) {
                    return ObjectUtils.nullSafeEquals(entry.getValue(), other.getValue());
                }
            }

            return false;
        }

        public boolean remove(Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> entry = (Map.Entry)o;
                return ConcurrentReferenceHashMap.this.remove(entry.getKey(), entry.getValue());
            } else {
                return false;
            }
        }

        public int size() {
            return ConcurrentReferenceHashMap.this.size();
        }

        public void clear() {
            ConcurrentReferenceHashMap.this.clear();
        }
    }

    private abstract class Entries {
        private Entries() {
        }

        public abstract void add(V var1);
    }

    private static enum TaskOption {
        RESTRUCTURE_BEFORE,
        RESTRUCTURE_AFTER,
        SKIP_IF_EMPTY,
        RESIZE;

        private TaskOption() {
        }
    }

    private abstract class Task<T> {
        private final EnumSet<TaskOption> options;

        public Task(TaskOption... options) {
            this.options = options.length == 0 ? EnumSet.noneOf(TaskOption.class) : EnumSet.of(options[0], options);
        }

        public boolean hasOption(TaskOption option) {
            return this.options.contains(option);
        }

        protected T execute(Reference<K, V> reference, Entry<K, V> entry, Entries entries) {
            return this.execute(reference, entry);
        }

        protected T execute(Reference<K, V> reference, Entry<K, V> entry) {
            return null;
        }
    }

    protected static final class Entry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private volatile V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public V setValue(V value) {
            V previous = this.value;
            this.value = value;
            return previous;
        }

        public String toString() {
            return this.key + "=" + this.value;
        }

        public final boolean equals(Object other) {
            if (this == other) {
                return true;
            } else if (!(other instanceof Map.Entry)) {
                return false;
            } else {
                Map.Entry otherEntry = (Map.Entry)other;
                return ObjectUtils.nullSafeEquals(this.getKey(), otherEntry.getKey()) && ObjectUtils.nullSafeEquals(this.getValue(), otherEntry.getValue());
            }
        }

        public final int hashCode() {
            return ObjectUtils.nullSafeHashCode(this.key) ^ ObjectUtils.nullSafeHashCode(this.value);
        }
    }

    protected interface Reference<K, V> {
        Entry<K, V> get();

        int getHash();

        Reference<K, V> getNext();

        void release();
    }

    protected final class Segment extends ReentrantLock {
        private final ReferenceManager referenceManager = ConcurrentReferenceHashMap.this.createReferenceManager();
        private final int initialSize;
        private volatile Reference<K, V>[] references;
        private volatile int count = 0;
        private int resizeThreshold;

        public Segment(int initialCapacity) {
            this.initialSize = 1 << ConcurrentReferenceHashMap.calculateShift(initialCapacity, 1073741824);
            this.setReferences(this.createReferenceArray(this.initialSize));
        }

        public Reference<K, V> getReference(Object key, int hash, Restructure restructure) {
            if (restructure == Restructure.WHEN_NECESSARY) {
                this.restructureIfNecessary(false);
            }

            if (this.count == 0) {
                return null;
            } else {
                Reference<K, V>[] references = this.references;
                int index = this.getIndex(hash, references);
                Reference<K, V> head = references[index];
                return this.findInChain(head, key, hash);
            }
        }

        public <T> T doTask(final int hash, final Object key, Task<T> task) {
            boolean resize = task.hasOption(TaskOption.RESIZE);
            if (task.hasOption(TaskOption.RESTRUCTURE_BEFORE)) {
                this.restructureIfNecessary(resize);
            }

            if (task.hasOption(TaskOption.SKIP_IF_EMPTY) && this.count == 0) {
                return (T) task.execute((Reference)null, (Entry)null, (Entries)null);
            } else {
                this.lock();

                Object var10;
                try {
                    final int index = this.getIndex(hash, this.references);
                    final Reference<K, V> head = this.references[index];
                    Reference<K, V> reference = this.findInChain(head, key, hash);
                    Entry<K, V> entry = reference != null ? reference.get() : null;
                    Entries entries = new Entries() {
                        public void add(V value) {
                            Entry<K, V> newEntry = new Entry(key, value);
                            Reference<K, V> newReference = Segment.this.referenceManager.createReference(newEntry, hash, head);
                            Segment.this.references[index] = newReference;
                            Segment.this.count++;
                        }
                    };
                    var10 = task.execute(reference, entry, entries);
                } finally {
                    this.unlock();
                    if (task.hasOption(TaskOption.RESTRUCTURE_AFTER)) {
                        this.restructureIfNecessary(resize);
                    }

                }

                return (T) var10;
            }
        }

        public void clear() {
            if (this.count != 0) {
                this.lock();

                try {
                    this.setReferences(this.createReferenceArray(this.initialSize));
                    this.count = 0;
                } finally {
                    this.unlock();
                }

            }
        }

        protected final void restructureIfNecessary(boolean allowResize) {
            boolean needsResize = this.count > 0 && this.count >= this.resizeThreshold;
            Reference<K, V> reference = this.referenceManager.pollForPurge();
            if (reference != null || needsResize && allowResize) {
                this.lock();

                try {
                    int countAfterRestructure = this.count;
                    Set<Reference<K, V>> toPurge = Collections.emptySet();
                    if (reference != null) {
                        for(toPurge = new HashSet(); reference != null; reference = this.referenceManager.pollForPurge()) {
                            ((Set)toPurge).add(reference);
                        }
                    }

                    countAfterRestructure -= ((Set)toPurge).size();
                    needsResize = countAfterRestructure > 0 && countAfterRestructure >= this.resizeThreshold;
                    boolean resizing = false;
                    int restructureSize = this.references.length;
                    if (allowResize && needsResize && restructureSize < 1073741824) {
                        restructureSize <<= 1;
                        resizing = true;
                    }

                    Reference<K, V>[] restructured = resizing ? this.createReferenceArray(restructureSize) : this.references;

                    for(int i = 0; i < this.references.length; ++i) {
                        reference = this.references[i];
                        if (!resizing) {
                            restructured[i] = null;
                        }

                        for(; reference != null; reference = reference.getNext()) {
                            if (!((Set)toPurge).contains(reference) && reference.get() != null) {
                                int index = this.getIndex(reference.getHash(), restructured);
                                restructured[index] = this.referenceManager.createReference(reference.get(), reference.getHash(), restructured[index]);
                            }
                        }
                    }

                    if (resizing) {
                        this.setReferences(restructured);
                    }

                    this.count = Math.max(countAfterRestructure, 0);
                } finally {
                    this.unlock();
                }
            }

        }

        private Reference<K, V> findInChain(Reference<K, V> reference, Object key, int hash) {
            for(; reference != null; reference = reference.getNext()) {
                if (reference.getHash() == hash) {
                    Entry<K, V> entry = reference.get();
                    if (entry != null) {
                        K entryKey = entry.getKey();
                        if (entryKey == key || entryKey.equals(key)) {
                            return reference;
                        }
                    }
                }
            }

            return null;
        }

        private Reference<K, V>[] createReferenceArray(int size) {
            return (Reference[])((Reference[])Array.newInstance(Reference.class, size));
        }

        private int getIndex(int hash, Reference<K, V>[] references) {
            return hash & references.length - 1;
        }

        private void setReferences(Reference<K, V>[] references) {
            this.references = references;
            this.resizeThreshold = (int)((float)references.length * ConcurrentReferenceHashMap.this.getLoadFactor());
        }

        public final int getSize() {
            return this.references.length;
        }

        public final int getCount() {
            return this.count;
        }
    }

    public static enum ReferenceType {
        SOFT,
        WEAK;

        private ReferenceType() {
        }
    }
}
