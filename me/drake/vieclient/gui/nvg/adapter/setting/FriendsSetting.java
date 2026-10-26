package me.drake.vieclient.gui.nvg.adapter.setting;

import java.util.ArrayList;
import java.util.List;

public class FriendsSetting extends Setting {
   private final List friends = new ArrayList();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public FriendsSetting(CharSequence name, List defaultFriends) {
      super(name);
      if (defaultFriends != null) {
         this.friends.addAll(defaultFriends);
      }
   }

   public FriendsSetting(CharSequence name) {
      this(name, null);
   }

   public List getFriends() {
      return new ArrayList(this.friends);
   }

   public void addFriend(String name) {
      if (name != null && !name.trim().isEmpty()) {
         name = name.trim().toLowerCase();
         if (!this.friends.contains(name)) {
            this.friends.add(name);
         }
      }
   }

   public void removeFriend(String name) {
      this.friends.remove(name);
   }

   public void clearFriends() {
      this.friends.clear();
   }

   public boolean isFriend(String name) {
      return this.friends.contains(name.toLowerCase());
   }

   public void setFriends(List s) {
      this.friends.clear();
      if (s != null) {
         this.friends.addAll(s);
      }
   }

   public List getDefaultFriends() {
      return new ArrayList();
   }

   @Override
   public void resetValue() {
      this.friends.clear();
   }

   public int size() {
      return this.friends.size();
   }

   public FriendsSetting setDescription(CharSequence s) {
      super.setDescription(s);
      return this;
   }
}
