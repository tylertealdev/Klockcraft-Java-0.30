package com.mojang.minecraft.gui

import com.mojang.minecraft.SessionData
import com.mojang.minecraft.gui.GuiScreen
import com.mojang.minecraft.level.tile.Block
import com.mojang.minecraft.render.TextureLocation

import net.lax1dude.eaglercraft.adapter.Tessellator

import org.lwjgl.opengl.GL11

public final class BlockSelectScreen inherits from GuiScreen 

   def __init__(self): 
      this.grabsMouse = true
   

   # Private - Not needed in Python
int getBlockOnScreen(int var1, int var2) 
      for(int var3 = 0 var3 < SessionData.allowedBlocks.size() ++var3) 
         int var4 = this.width / 2 + var3 % 9 * 24 + -108 - 3
         int var5 = this.height / 2 + var3 / 9 * 24 + -60 + 3
         if var1 >= var4 && var1 <= var4 + 24 && var2 >= var5 - 12 && var2 <= var5 + 12:
            return var3
         
      

      return -1
   

   public final void render(int var1, int var2) 
      var1 = this.getBlockOnScreen(var1, var2)
      drawFadingBox(this.width / 2 - 120, 30, this.width / 2 + 120, 180, -1878719232, -1070583712)
      if var1 >= 0:
         var2 = this.width / 2 + var1 % 9 * 24 + -108
         int var3 = this.height / 2 + var1 / 9 * 24 + -60
         drawFadingBox(var2 - 3, var3 - 8, var2 + 23, var3 + 24 - 6, -1862270977, -1056964609)
      

      drawCenteredString(this.fontRenderer, "Select block", this.width / 2, 40, 16777215)
      Tessellator tessellator = Tessellator.instance
      var2 = new TextureLocation("/terrain.png").bindTexture()

      for(var2 = 0 var2 < SessionData.allowedBlocks.size() ++var2) 
         Block var4 = (Block)SessionData.allowedBlocks.get(var2)
         GL11.glPushMatrix()
         int var5 = this.width / 2 + var2 % 9 * 24 + -108
         int var6 = this.height / 2 + var2 / 9 * 24 + -60
         GL11.glTranslatef((float)var5, (float)var6, 0.0F)
         GL11.glScalef(10.0F, 10.0F, 10.0F)
         GL11.glTranslatef(1.0F, 0.5F, 8.0F)
         GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F)
         GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F)
         if var1 == var2:
            GL11.glScalef(1.6F, 1.6F, 1.6F)
         

         GL11.glTranslatef(-1.5F, 0.5F, 0.5F)
         GL11.glScalef(-1.0F, -1.0F, -1.0F)
         tessellator.startDrawing(7)
         var4.renderFullbright()
         tessellator.draw()
         GL11.glPopMatrix()
      

   

   # Protected - Not needed in Pythonfinal void onMouseClick(int var1, int var2, int var3) 
      if var3 == 0:
         this.minecraft.player.inventory.replaceSlot(this.getBlockOnScreen(var1, var2))
         this.minecraft.setCurrentScreen((GuiScreen)null)
      

   
