package com.mojang.minecraft.gui

import com.mojang.minecraft.gui.Screen

class Button: inherits from Screen 

   int width
   int height
   public int x
   public int y
   public str text
   public int id
   public bool active
   public bool visible


   public Button(int var1, int var2, int var3, str var4) 
      this(var1, var2, var3, 200, 20, var4)
   

   Button(int var1, int var2, int var3, int var4, int var5, str var6) 
      this.width = 200
      this.height = 20
      this.active = true
      this.visible = true
      this.id = var1
      this.x = var2
      this.y = var3
      this.width = var4
      this.height = 20
      this.text = var6
   
