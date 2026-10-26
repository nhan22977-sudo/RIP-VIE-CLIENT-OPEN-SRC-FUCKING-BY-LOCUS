package io.github.kosmx.emotes.common.tools;

import it.unimi.dsi.fastutil.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;

public class BiMap<L, R> implements Collection<Pair<L, R>> {
   final HashMap<L, R> lToR = new HashMap<>();
   final HashMap<R, L> rToL = new HashMap<>();
   final HashSet<Pair<L, R>> collection = new HashSet<>();

   public R getR(L key) {
      return this.lToR.get(key);
   }

   public L getL(R key) {
      return this.rToL.get(key);
   }

   public boolean containsL(L l) {
      return this.lToR.containsKey(l);
   }

   public boolean containsR(R r) {
      return this.rToL.containsKey(r);
   }

   public Pair<L, R> put(L l, R r) {
      if (l != null && r != null) {
         L ol = null;
         R or = null;
         if (this.lToR.containsKey(l) || this.rToL.containsKey(r)) {
            L var5 = this.rToL.remove(r);
            or = this.lToR.remove(l);
            ol = (L)(var5 != null ? var5 : this.rToL.remove(or));
            or = or != null ? or : this.lToR.remove(ol);
            this.collection.removeIf(obj -> obj.left().equals(l) || obj.right().equals(r));
         }

         this.rToL.put(r, l);
         this.lToR.put(l, r);
         this.collection.add(Pair.of(l, r));
         return Pair.of(ol, or);
      } else {
         throw new NullPointerException("BiMap does not allow null elements");
      }
   }

   public boolean add(Pair<L, R> pair) {
      if (pair == null || pair.left() == null || pair.right() == null) {
         throw new NullPointerException("BiMap does not allow null elements");
      }

      if (this.collection.contains(pair)) {
         return false;
      }

      this.put((L)pair.left(), (R)pair.right());
      return true;
   }

   public R removeL(L l) {
      if (l == null) {
         throw new NullPointerException("BiMap does not allow null elements");
      }

      R r = this.lToR.remove(l);
      if (r != null) {
         this.rToL.remove(r);
         this.collection.remove(Pair.of(l, r));
      }

      return r;
   }

   public L removeR(R r) {
      if (r == null) {
         throw new NullPointerException("BiMap does not allow null elements");
      }

      L l = this.rToL.remove(r);
      if (l != null) {
         this.rToL.remove(r);
         this.collection.remove(Pair.of(l, r));
      }

      return l;
   }

   @Override
   public boolean remove(Object o) {
      boolean bl = this.collection.remove(o);
      if (bl) {
         Pair<L, R> pair = (Pair<L, R>)o;
         this.lToR.remove(pair.left());
         this.rToL.remove(pair.right());
      }

      return bl;
   }

   @Override
   public boolean containsAll(@NotNull Collection<?> c) {
      return this.collection.containsAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends Pair<L, R>> c) {
      boolean bl = false;

      for (Pair<L, R> pair : c) {
         bl |= this.add(pair);
      }

      return bl;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      boolean bl = false;

      for (Object pair : c) {
         bl |= this.remove(pair);
      }

      return bl;
   }

   @Override
   public boolean retainAll(@NotNull Collection<?> c) {
      boolean modified = false;
      Iterator<Pair<L, R>> it = this.iterator();

      while (it.hasNext()) {
         if (!c.contains(it.next())) {
            it.remove();
            modified = true;
         }
      }

      return modified;
   }

   @Override
   public void clear() {
      this.lToR.clear();
      this.rToL.clear();
      this.collection.clear();
   }

   @Override
   public int size() {
      return this.collection.size();
   }

   @Override
   public boolean isEmpty() {
      return this.collection.isEmpty();
   }

   @Override
   public boolean contains(Object o) {
      return this.collection.contains(o);
   }

   @Override
   public Iterator<Pair<L, R>> iterator() {
      return new Iterator<Pair<L, R>>() {
         final Iterator<Pair<L, R>> _iter = BiMap.this.collection.iterator();
         Pair<L, R> _var = null;

         @Override
         public boolean hasNext() {
            return this._iter.hasNext();
         }

         public Pair<L, R> next() {
            return this._var = this._iter.next();
         }

         @Override
         public void remove() {
            this._iter.remove();
            BiMap.this.lToR.remove(this._var.left());
            BiMap.this.rToL.remove(this._var.right());
         }
      };
   }

   @Override
   public Object[] toArray() {
      return this.collection.toArray();
   }

   @Override
   public <T> T[] toArray(T[] a) {
      return (T[])this.collection.toArray(a);
   }

   private void idk() {
      this.iterator().remove();
   }

   @Override
   public int hashCode() {
      return this.collection.hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof BiMap ? this.collection.equals(((BiMap)obj).collection) : false;
   }
}
