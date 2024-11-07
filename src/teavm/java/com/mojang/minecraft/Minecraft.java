package com.mojang.minecraft

import com.mojang.minecraft.gamemode.CreativeGameMode
import com.mojang.minecraft.gamemode.GameMode
import com.mojang.minecraft.gamemode.SurvivalGameMode
import com.mojang.minecraft.gui.*
import com.mojang.minecraft.item.Arrow
import com.mojang.minecraft.item.Item
import com.mojang.minecraft.level.Level
import com.mojang.minecraft.level.LevelIO
import com.mojang.minecraft.level.generator.LevelGenerator
import com.mojang.minecraft.level.liquid.LiquidType
import com.mojang.minecraft.level.tile.Block
import com.mojang.minecraft.mob.Mob
import com.mojang.minecraft.model.HumanoidModel
import com.mojang.minecraft.model.ModelManager
import com.mojang.minecraft.model.ModelPart
import com.mojang.minecraft.model.Vec3D
import com.mojang.minecraft.particle.Particle
import com.mojang.minecraft.particle.ParticleManager
import com.mojang.minecraft.particle.WaterDropParticle
import com.mojang.minecraft.phys.AABB
import com.mojang.minecraft.player.InputHandlerImpl
import com.mojang.minecraft.player.Player
import com.mojang.minecraft.render.*
import com.mojang.minecraft.render.Renderer
import net.PeytonPlayz585.math.MathHelper
import net.PeytonPlayz585.music.MusicThread
import net.lax1dude.eaglercraft.GLAllocation
import net.lax1dude.eaglercraft.adapter.Tessellator

import org.lwjgl.opengl.GL11

import java.awt.*
import java.io.*
import java.nio.IntBuffer
import java.util.Collections
import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.Random
import java.util.Map.Entry

public final class Minecraft implements Runnable 

   public GameMode gamemode = new SurvivalGameMode(this)
   public int width
   public int height
   public Timer timer = new Timer(20.0F)
   public Level level
   public LevelRenderer levelRenderer
   public Player player
   public ParticleManager particleManager
   public SessionData session = null
   public str host
   public bool levelLoaded = false
   public volatile bool waiting = false
   public FontRenderer fontRenderer
   public GuiScreen currentScreen = null
   public ProgressBarDisplay progressBar = new ProgressBarDisplay(this)
   public Renderer renderer = new Renderer(this)
   public LevelIO levelIo
   # Private - Not needed in Pythonint ticks
   # Private - Not needed in Pythonint blockHitTime
   public str levelName
   public int levelId
   public HUDScreen hud
   public bool online
   public MovingObjectPosition selected
   public static GameSettings settings
   str server
   int port
   volatile bool running
   public str debug
   public bool hasMouse
   # Private - Not needed in Pythonint lastClick
   public bool raining
   # Private - Not needed in Pythonstatic Tessellator tessellator = Tessellator.instance
   public static Minecraft minecraft
   public Entity field_22009_h
   public int timeSinceLastSaved = 0


   def __init__(self): 
      this.levelIo = new LevelIO(this.progressBar)
      this.ticks = 0
      this.blockHitTime = 0
      this.levelName = null
      this.levelId = 0
      this.online = false
      new HumanoidModel(0.0F)
      this.selected = null
      this.server = null
      this.port = 0
      this.running = false
      this.debug = ""
      this.hasMouse = false
      this.lastClick = 0
      this.raining = false
      new SleepForeverThread(this)
      this.width = GL11.getCanvasWidth()
      this.height = GL11.getCanvasHeight()
   

   public final void setCurrentScreen(GuiScreen var1) 
      if !(this.currentScreen instanceof ErrorScreen):
         if this.currentScreen != null:
            this.currentScreen.onClose()
         

         if var1 == null && this.player.health <= 0:
            var1 = new GameOverScreen()
         

         this.currentScreen = (GuiScreen)var1
         if var1 != null:
            if this.hasMouse:
               this.player.releaseAllKeys()
               this.hasMouse = false
               GL11.mouseSetGrabbed(false)
            

            int var2 = this.width * 240 / this.height
            int var3 = this.height * 240 / this.height
            ((GuiScreen)var1).open(this, var2, var3)
            this.online = false
          else:
            this.grabMouse()
         
      
   

   # Private - Not needed in Pythonstatic void checkGLError(str var0) 
      int var1
      if (var1 = GL11.glGetError()) != 0:
         str var2 = GL11.gluErrorString(var1)
         System.out.println("########## GL ERROR ##########")
         System.out.println("@ " + var0)
         System.out.println(var1 + ": " + var2)
      

   

   public final void shutdown() 
      GL11.destroyContext()
   

   public final void run() 
      this.running = true
         Minecraft var1 = this
         checkGLError("Pre startup")
         GL11.glEnable(3553)
         GL11.glShadeModel(7425)
         GL11.glClearDepth((float)1.0D)
         GL11.glEnable(2929)
         GL11.glDepthFunc(515)
         GL11.glEnable(3008)
         GL11.glAlphaFunc(516, 0.0F)
         GL11.glCullFace(1029)
         GL11.glMatrixMode(5889)
         GL11.glLoadIdentity()
         GL11.glMatrixMode(5888)
         checkGLError("Startup")
         this.settings = new GameSettings(this)
         this.fontRenderer = new FontRenderer(this.settings, "/default.png")
         IntBuffer var9
         (var9 = GLAllocation.createDirectIntBuffer(256)).clear().limit(256)
         this.levelRenderer = new LevelRenderer(this)
         Item.initModels()
         Mob.modelCache = new ModelManager()
         GL11.glViewport(0, 0, this.width, this.height)
         this.generateLevel(1)
         this.particleManager = new ParticleManager(this.level)
         checkGLError("Post startup")
         this.hud = new HUDScreen(this, this.width, this.height)

      long var13 = System.currentTimeMillis()
      int var15 = 0
         while this.running:
            if this.waiting:
               try 
				Thread.sleep(100L)
			 catch (InterruptedException e) 
				e.printStackTrace()
			
             else:
                  Timer var63 = this.timer
                  long var16
                  long var18 = (var16 = System.currentTimeMillis()) - var63.lastSysClock
                  long var20 = System.nanoTime() / 1000000L
                  float var24
                  if var18 > 1000L:
                     long var22 = var20 - var63.lastHRClock
                     var24 = (float)var18 / (float)var22
                     var63.adjustment += (var24 - var63.adjustment) * 0.20000000298023224D
                     var63.lastSysClock = var16
                     var63.lastHRClock = var20
                  

                  if var18 < 0L:
                     var63.lastSysClock = var16
                     var63.lastHRClock = var20
                  

                  float var95
                  var24 = ((var95 = (float)var20 / 1000.0D) - var63.lastHR) * var63.adjustment
                  var63.lastHR = var95
                  if var24 < 0.0D:
                     var24 = 0.0D
                  

                  if var24 > 1.0D:
                     var24 = 1.0D
                  

                  var63.elapsedDelta = (float)((float)var63.elapsedDelta + var24 * (float)var63.speed * (float)var63.tps)
                  var63.elapsedTicks = (int)var63.elapsedDelta
                  if var63.elapsedTicks > 100:
                     var63.elapsedTicks = 100
                  

                  var63.elapsedDelta -= (float)var63.elapsedTicks
                  var63.delta = var63.elapsedDelta

                  for(int var64 = 0 var64 < this.timer.elapsedTicks ++var64) 
                     ++this.ticks
                     this.tick()
                  
                  
                  if !hasMouse & GL11.isPointerLocked2() && this.currentScreen == null:
                	  hasMouse = true
                   else if !GL11.isPointerLocked2():
                	  hasMouse = false
                	  this.ungrabMouse()
                  
                  
                  if this.width != GL11.parent.getClientWidth() || this.height != GL11.parent.getClientHeight() || settings.fullscreen && !GL11.isPointerLocked2():
                	  GuiScreen gui = this.currentScreen
                	  this.width = GL11.parent.getClientWidth()
                	  this.height = GL11.parent.getClientHeight()
                	  GL11.canvas.setWidth(width)
                	  GL11.canvas.setHeight(height)
                	  GL11.updateDisplay()
                	  if settings.fullscreen && !GL11.isPointerLocked2():
                		  settings.fullscreen = false
                	  
                	  if currentScreen != null:
              			ScaledResolution scaledresolution = new ScaledResolution(width, height)
              			int k = scaledresolution.getScaledWidth()
              			int l = scaledresolution.getScaledHeight()
              			currentScreen.setWorldAndResolution(this, k, l, gui)
              		
                  

                  checkGLError("Pre render")
                  GL11.glEnable(3553)
                  if !this.online:
                     this.gamemode.applyCracks(this.timer.delta)
                     float var65 = this.timer.delta
                     Renderer var66 = this.renderer
                     if this.renderer.displayActive && !GL11.isFocused():
                        var66.minecraft.pause()
                     

                     var66.displayActive = GL11.isFocused()
                     int var68
                     int var70
                     int var86
                     int var81
                     if var66.minecraft.hasMouse:
                    	var81 = GL11.mouseGetDX()
                        var86 = GL11.mouseGetDY()

                        byte var91 = 1
                        if var66.minecraft.settings.invertMouse:
                           var91 = -1
                        

                        var66.minecraft.player.turn((float)var81, (float)(var86 * var91))
                     

                     if !var66.minecraft.online:
                        var81 = var66.minecraft.width * 240 / var66.minecraft.height
                        var86 = var66.minecraft.height * 240 / var66.minecraft.height
                        int var94 = GL11.mouseGetX() * var81 / var66.minecraft.width
                        var70 = var86 - GL11.mouseGetY() * var86 / var66.minecraft.height - 1
                        if var66.minecraft.level != null:
                           float var80 = var65
                           Renderer var82 = var66
                           Renderer var27 = var66
                           Player var28
                           float var29 = (var28 = var66.minecraft.player).xRotO + (var28.xRot - var28.xRotO) * var65
                           float var30 = var28.yRotO + (var28.yRot - var28.yRotO) * var65
                           Vec3D var31 = var66.getPlayerVector(var65)
                           float var32 = MathHelper.cos(-var30 * 0.017453292F - 3.1415927F)
                           float var69 = MathHelper.sin(-var30 * 0.017453292F - 3.1415927F)
                           float var74 = MathHelper.cos(-var29 * 0.017453292F)
                           float var33 = MathHelper.sin(-var29 * 0.017453292F)
                           float var34 = var69 * var74
                           float var87 = var32 * var74
                           float var36 = var66.minecraft.gamemode.getReachDistance()
                           Vec3D var71 = var31.add(var34 * var36, var33 * var36, var87 * var36)
                           var66.minecraft.selected = var66.minecraft.level.clip(var31, var71)
                           var74 = var36
                           if var66.minecraft.selected != null:
                              var74 = var66.minecraft.selected.vec.distance(var66.getPlayerVector(var65))
                           

                           var31 = var66.getPlayerVector(var65)
                           if var66.minecraft.gamemode instanceof CreativeGameMode:
                              var36 = 32.0F
                            else:
                              var36 = var74
                           

                           var71 = var31.add(var34 * var36, var33 * var36, var87 * var36)
                           var66.entity = null
                           List var37 = var66.minecraft.level.blockMap.getEntities(var28, var28.bb.expand(var34 * var36, var33 * var36, var87 * var36))
                           float var35 = 0.0F

                           for(var81 = 0 var81 < var37.size() ++var81) 
                              Entity var88
                              if (var88 = (Entity)var37.get(var81)).isPickable():
                                 var74 = 0.1F
                                 MovingObjectPosition var78
                                 if (var78 = var88.bb.grow(var74, var74, var74).clip(var31, var71)) != null && ((var74 = var31.distance(var78.vec)) < var35 || var35 == 0.0F):
                                    var27.entity = var88
                                    var35 = var74
                                 
                              
                           

                           if var27.entity != null && !(var27.minecraft.gamemode instanceof CreativeGameMode):
                              var27.minecraft.selected = new MovingObjectPosition(var27.entity)
                           

                           int var77 = 0

                           while true:
                              if var77 >= 2:
                                 GL11.glColorMask(true, true, true, false)
                                 break
                              

                              if var82.minecraft.settings.anaglyph:
                                 if var77 == 0:
                                    GL11.glColorMask(false, true, true, false)
                                  else:
                                    GL11.glColorMask(true, false, false, false)
                                 
                              

                              Player var126 = var82.minecraft.player
                              Level var119 = var82.minecraft.level
                              LevelRenderer var89 = var82.minecraft.levelRenderer
                              ParticleManager var93 = var82.minecraft.particleManager
                              GL11.glViewport(0, 0, var82.minecraft.width, var82.minecraft.height)
                              Level var26 = var82.minecraft.level
                              var28 = var82.minecraft.player
                              var29 = 1.0F / (float)(4 - var82.minecraft.settings.viewDistance)
                              var29 = 1.0F - (float)Math.pow((float)var29, 0.25D)
                              var30 = (float)(var26.skyColor >> 16 & 255) / 255.0F
                              float var117 = (float)(var26.skyColor >> 8 & 255) / 255.0F
                              var32 = (float)(var26.skyColor & 255) / 255.0F
                              var82.fogRed = (float)(var26.fogColor >> 16 & 255) / 255.0F
                              var82.fogBlue = (float)(var26.fogColor >> 8 & 255) / 255.0F
                              var82.fogGreen = (float)(var26.fogColor & 255) / 255.0F
                              var82.fogRed += (var30 - var82.fogRed) * var29
                              var82.fogBlue += (var117 - var82.fogBlue) * var29
                              var82.fogGreen += (var32 - var82.fogGreen) * var29
                              var82.fogRed *= var82.fogColorMultiplier
                              var82.fogBlue *= var82.fogColorMultiplier
                              var82.fogGreen *= var82.fogColorMultiplier
                              Block var73
                              if (var73 = Block.blocks[var26.getTile((int)var28.x, (int)(var28.y + 0.12F), (int)var28.z)]) != null && var73.getLiquidType() != LiquidType.NOT_LIQUID:
                                 LiquidType var79
                                 if (var79 = var73.getLiquidType()) == LiquidType.WATER:
                                    var82.fogRed = 0.02F
                                    var82.fogBlue = 0.02F
                                    var82.fogGreen = 0.2F
                                  else if var79 == LiquidType.LAVA:
                                    var82.fogRed = 0.6F
                                    var82.fogBlue = 0.1F
                                    var82.fogGreen = 0.0F
                                 
                              

                              if var82.minecraft.settings.anaglyph:
                                 var74 = (var82.fogRed * 30.0F + var82.fogBlue * 59.0F + var82.fogGreen * 11.0F) / 100.0F
                                 var33 = (var82.fogRed * 30.0F + var82.fogBlue * 70.0F) / 100.0F
                                 var34 = (var82.fogRed * 30.0F + var82.fogGreen * 70.0F) / 100.0F
                                 var82.fogRed = var74
                                 var82.fogBlue = var33
                                 var82.fogGreen = var34
                              

                              GL11.glClearColor(var82.fogRed, var82.fogBlue, var82.fogGreen, 0.0F)
                              GL11.glClear(16640)
                              var82.fogColorMultiplier = 1.0F
                              GL11.glEnable(2884)
                              var82.fogEnd = (float)(512 >> (var82.minecraft.settings.viewDistance << 1))
                              GL11.glMatrixMode(5889)
                              GL11.glLoadIdentity()
                              var29 = 0.07F
                              if var82.minecraft.settings.anaglyph:
                                 GL11.glTranslatef((float)(-((var77 << 1) - 1)) * var29, 0.0F, 0.0F)
                              

                              Player var116 = var82.minecraft.player
                              var69 = 70.0F
                              if var116.health <= 0:
                                 var74 = (float)var116.deathTime + var80
                                 var69 /= (1.0F - 500.0F / (var74 + 500.0F)) * 2.0F + 1.0F
                              

                              GL11.gluPerspective(var69, (float)var82.minecraft.width / (float)var82.minecraft.height, 0.05F, var82.fogEnd)
                              GL11.glMatrixMode(5888)
                              GL11.glLoadIdentity()
                              if var82.minecraft.settings.anaglyph:
                                 GL11.glTranslatef((float)((var77 << 1) - 1) * 0.1F, 0.0F, 0.0F)
                              

                              var82.hurtEffect(var80)
                              if var82.minecraft.settings.viewBobbing:
                                 var82.applyBobbing(var80)
                              

                              var116 = var82.minecraft.player
                              GL11.glTranslatef(0.0F, 0.0F, -0.1F)
                              GL11.glRotatef(var116.xRotO + (var116.xRot - var116.xRotO) * var80, 1.0F, 0.0F, 0.0F)
                              GL11.glRotatef(var116.yRotO + (var116.yRot - var116.yRotO) * var80, 0.0F, 1.0F, 0.0F)
                              var69 = var116.xo + (var116.x - var116.xo) * var80
                              var74 = var116.yo + (var116.y - var116.yo) * var80
                              var33 = var116.zo + (var116.z - var116.zo) * var80
                              GL11.glTranslatef(-var69, -var74, -var33)
                              Frustrum var76 = new Frustrum()
                              if field_22009_h != this.player:
                            	  field_22009_h = player
                            	  float f = this.timer.elapsedTicks
                            	  float d = field_22009_h.x + (field_22009_h.x - field_22009_h.x) * (float) f
                          		  float d1 = field_22009_h.y + (field_22009_h.y - field_22009_h.y) * (float) f
                          		  float d2 = field_22009_h.z + (field_22009_h.z - field_22009_h.z) * (float) f
                            	  ClippingHelperImplementation.getInstance()
                            	  Frustrum frustrum = new Frustrum()
                            	  frustrum.setPosition(d, d1, d2)
                            	  var76 = frustrum
                            	  System.out.println("yee")
                              
                              LevelRenderer var101 = var82.minecraft.levelRenderer

                              int var98
                              for(var98 = 0 var98 < var101.chunkCache.length ++var98) 
                                 var101.chunkCache[var98].clip(var76)
                              

                              var101 = var82.minecraft.levelRenderer
                              Collections.sort(var82.minecraft.levelRenderer.chunks, new ChunkDirtyDistanceComparator(var126))
                              var98 = var101.chunks.size() - 1
                              int var105
                              if (var105 = var101.chunks.size()) > 3:
                                 var105 = 3
                              

                              int var104
                              for(var104 = 0 var104 < var105 ++var104) 
                                 Chunk var118
                                 (var118 = (Chunk)var101.chunks.remove(var98 - var104)).update()
                                 var118.loaded = false
                              

                              var82.updateFog()
                              GL11.glEnable(2912)
                              var89.sortChunks(var126, 0)
                              int var83
                              int var110
                              int var114
                              int var125
                              int var122
                              int var120
                              if var119.isSolid(var126.x, var126.y, var126.z, 0.1F):
                                 var120 = (int)var126.x
                                 var83 = (int)var126.y
                                 var110 = (int)var126.z

                                 for(var122 = var120 - 1 var122 <= var120 + 1 ++var122) 
                                    for(var125 = var83 - 1 var125 <= var83 + 1 ++var125) 
                                       for(int var38 = var110 - 1 var38 <= var110 + 1 ++var38) 
                                          var105 = var38
                                          var98 = var125
                                          int var99 = var122
                                          if (var104 = var89.level.getTile(var122, var125, var38)) != 0 && Block.blocks[var104].isSolid():
                                             GL11.glColor4f(0.2F, 0.2F, 0.2F, 1.0F)
                                             GL11.glDepthFunc(513)
                                             tessellator.startDrawing(7)

                                             for(var114 = 0 var114 < 6 ++var114) 
                                                Block.blocks[var104].renderInside(var99, var98, var105, var114)
                                             

                                             tessellator.draw()
                                             GL11.glCullFace(1028)
                                             tessellator.startDrawing(7)

                                             for(var114 = 0 var114 < 6 ++var114) 
                                                Block.blocks[var104].renderInside(var99, var98, var105, var114)
                                             

                                             tessellator.draw()
                                             GL11.glCullFace(1029)
                                             GL11.glDepthFunc(515)
                                          
                                       
                                    
                                 
                              

                              var82.setLighting(true)
                              Vec3D var103 = var82.getPlayerVector(var80)
                              var89.level.blockMap.render(var103, var76, var80)
                              var82.setLighting(false)
                              var82.updateFog()
                              float var107 = var80
                              ParticleManager var96 = var93
                              var29 = -MathHelper.cos(var126.yRot * 3.1415927F / 180.0F)
                              var117 = -(var30 = -MathHelper.sin(var126.yRot * 3.1415927F / 180.0F)) * MathHelper.sin(var126.xRot * 3.1415927F / 180.0F)
                              var32 = var29 * MathHelper.sin(var126.xRot * 3.1415927F / 180.0F)
                              var69 = MathHelper.cos(var126.xRot * 3.1415927F / 180.0F)

                              for(var83 = 0 var83 < 2 ++var83) 
                                 if var96.particles[var83].size() != 0:
                                    var110 = 0
                                    if var83 == 0:
                                       var110 = new TextureLocation("/particles.png").bindTexture()
                                    

                                    if var83 == 1:
                                       var110 = new TextureLocation("/terrain.png").bindTexture()
                                    

                                    tessellator.startDrawing(7)

                                    for(var120 = 0 var120 < var96.particles[var83].size() ++var120) 
                                       ((Particle)var96.particles[var83].get(var120)).render(var107, var29, var69, var30, var117, var32)
                                    

                                    tessellator.draw()
                                 
                              

                              new TextureLocation("/rock.png").bindTexture()
                              GL11.glEnable(3553)
                              GL11.glCallList(var89.listId)
                              var82.updateFog()
                              var101 = var89
                              new TextureLocation("/clouds.png").bindTexture()
                              GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F)
                              var107 = (float)(var89.level.cloudColor >> 16 & 255) / 255.0F
                              var29 = (float)(var89.level.cloudColor >> 8 & 255) / 255.0F
                              var30 = (float)(var89.level.cloudColor & 255) / 255.0F
                              if var89.minecraft.settings.anaglyph:
                                 var117 = (var107 * 30.0F + var29 * 59.0F + var30 * 11.0F) / 100.0F
                                 var32 = (var107 * 30.0F + var29 * 70.0F) / 100.0F
                                 var69 = (var107 * 30.0F + var30 * 70.0F) / 100.0F
                                 var107 = var117
                                 var29 = var32
                                 var30 = var69
                              

                              var74 = 0.0F
                              var33 = 4.8828125E-4F
                              var74 = (float)(var89.level.depth + 2)
                              var34 = ((float)var89.ticks + var80) * var33 * 0.03F
                              var35 = 0.0F
                              tessellator.startDrawing(7)
                              tessellator.setColorOpaque_F(var107, var29, var30)

                              for(var86 = -2048 var86 < var101.level.width + 2048 var86 += 512) 
                                 for(var125 = -2048 var125 < var101.level.height + 2048 var125 += 512) 
                                	tessellator.addVertexWithUV((float)var86, var74, (float)(var125 + 512), (float)var86 * var33 + var34, (float)(var125 + 512) * var33)
                                    tessellator.addVertexWithUV((float)(var86 + 512), var74, (float)(var125 + 512), (float)(var86 + 512) * var33 + var34, (float)(var125 + 512) * var33)
                                    tessellator.addVertexWithUV((float)(var86 + 512), var74, (float)var125, (float)(var86 + 512) * var33 + var34, (float)var125 * var33)
                                    tessellator.addVertexWithUV((float)var86, var74, (float)var125, (float)var86 * var33 + var34, (float)var125 * var33)
                                    tessellator.addVertexWithUV((float)var86, var74, (float)var125, (float)var86 * var33 + var34, (float)var125 * var33)
                                    tessellator.addVertexWithUV((float)(var86 + 512), var74, (float)var125, (float)(var86 + 512) * var33 + var34, (float)var125 * var33)
                                    tessellator.addVertexWithUV((float)(var86 + 512), var74, (float)(var125 + 512), (float)(var86 + 512) * var33 + var34, (float)(var125 + 512) * var33)
                                    tessellator.addVertexWithUV((float)var86, var74, (float)(var125 + 512), (float)var86 * var33 + var34, (float)(var125 + 512) * var33)
                                 
                              

                              tessellator.draw()
                              GL11.glDisable(3553)
                              tessellator.startDrawing(7)
                              var34 = (float)(var101.level.skyColor >> 16 & 255) / 255.0F
                              var35 = (float)(var101.level.skyColor >> 8 & 255) / 255.0F
                              var87 = (float)(var101.level.skyColor & 255) / 255.0F
                              if var101.minecraft.settings.anaglyph:
                                 var36 = (var34 * 30.0F + var35 * 59.0F + var87 * 11.0F) / 100.0F
                                 var69 = (var34 * 30.0F + var35 * 70.0F) / 100.0F
                                 var74 = (var34 * 30.0F + var87 * 70.0F) / 100.0F
                                 var34 = var36
                                 var35 = var69
                                 var87 = var74
                              

                              tessellator.setColorOpaque_F(var34, var35, var87)
                              var74 = (float)(var101.level.depth + 10)

                              for(var125 = -2048 var125 < var101.level.width + 2048 var125 += 512) 
                                 for(var68 = -2048 var68 < var101.level.height + 2048 var68 += 512) 
                                	 tessellator.addVertex((float)var125, var74, (float)var68)
                                    tessellator.addVertex((float)(var125 + 512), var74, (float)var68)
                                    tessellator.addVertex((float)(var125 + 512), var74, (float)(var68 + 512))
                                    tessellator.addVertex((float)var125, var74, (float)(var68 + 512))
                                 
                              

                              tessellator.draw()
                              GL11.glEnable(3553)
                              var82.updateFog()
                              int var108
                              if var82.minecraft.selected != null:
                                 GL11.glDisable(3008)
                                 MovingObjectPosition var10001 = var82.minecraft.selected
                                 var105 = var126.inventory.getSelected()
                                 bool var106 = false
                                 MovingObjectPosition var102 = var10001
                                 var101 = var89
                                 GL11.glEnable(3042)
                                 GL11.glEnable(3008)
                                 GL11.glBlendFunc(770, 1)
                                 GL11.glColor4f(1.0F, 1.0F, 1.0F, (MathHelper.sin((float)System.currentTimeMillis() / 100.0F) * 0.2F + 0.4F) * 0.5F)
                                 if var89.cracks > 0.0F:
                                    GL11.glBlendFunc(774, 768)
                                    var108 = new TextureLocation("/terrain.png").bindTexture()
                                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F)
                                    GL11.glPushMatrix()
                                    Block var10000 = (var114 = var89.level.getTile(var102.x, var102.y, var102.z)) > 0?Block.blocks[var114]:null
                                    var73 = var10000
                                    var74 = (var10000.x1 + var73.x2) / 2.0F
                                    var33 = (var73.y1 + var73.y2) / 2.0F
                                    var34 = (var73.z1 + var73.z2) / 2.0F
                                    GL11.glTranslatef((float)var102.x + var74, (float)var102.y + var33, (float)var102.z + var34)
                                    var35 = 1.01F
                                    GL11.glScalef(1.01F, var35, var35)
                                    GL11.glTranslatef(-((float)var102.x + var74), -((float)var102.y + var33), -((float)var102.z + var34))
                                    tessellator.startDrawing(7)
                                    tessellator.disableColor()
                                    GL11.glDepthMask(false)
                                    if var73 == null:
                                       var73 = Block.STONE
                                    

                                    for(var86 = 0 var86 < 6 ++var86) 
                                       var73.renderSide(var102.x, var102.y, var102.z, var86, 240 + (int)(var101.cracks * 10.0F))
                                    

                                    tessellator.draw()
                                    GL11.glDepthMask(true)
                                    GL11.glPopMatrix()
                                 

                                 GL11.glDisable(3042)
                                 GL11.glDisable(3008)
                                 var10001 = var82.minecraft.selected
                                 var126.inventory.getSelected()
                                 var106 = false
                                 var102 = var10001
                                 GL11.glEnable(3042)
                                 GL11.glBlendFunc(770, 771)
                                 GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F)
                                 GL11.glLineWidth(2.0F)
                                 GL11.glDisable(3553)
                                 GL11.glDepthMask(false)
                                 var29 = 0.002F
                                 if (var104 = var89.level.getTile(var102.x, var102.y, var102.z)) > 0:
									 AABB var111 = Block.blocks[var104].getSelectionBox(var102.x, var102.y, var102.z).grow(var29, var29, var29)
									 tessellator.startDrawing(3)
									 tessellator.addVertex(var111.x0, var111.y0, var111.z0)
									 tessellator.addVertex(var111.x1, var111.y0, var111.z0)
									 tessellator.addVertex(var111.x1, var111.y0, var111.z1)
									 tessellator.addVertex(var111.x0, var111.y0, var111.z1)
									 tessellator.addVertex(var111.x0, var111.y0, var111.z0)
									 tessellator.draw()
									 tessellator.startDrawing(3)
									 tessellator.addVertex(var111.x0, var111.y1, var111.z0)
									 tessellator.addVertex(var111.x1, var111.y1, var111.z0)
									 tessellator.addVertex(var111.x1, var111.y1, var111.z1)
									 tessellator.addVertex(var111.x0, var111.y1, var111.z1)
									 tessellator.addVertex(var111.x0, var111.y1, var111.z0)
									 tessellator.draw()
									 tessellator.startDrawing(1)
									 tessellator.addVertex(var111.x0, var111.y0, var111.z0)
									 tessellator.addVertex(var111.x0, var111.y1, var111.z0)
									 tessellator.addVertex(var111.x1, var111.y0, var111.z0)
									 tessellator.addVertex(var111.x1, var111.y1, var111.z0)
									 tessellator.addVertex(var111.x1, var111.y0, var111.z1)
									 tessellator.addVertex(var111.x1, var111.y1, var111.z1)
									 tessellator.addVertex(var111.x0, var111.y0, var111.z1)
									 tessellator.addVertex(var111.x0, var111.y1, var111.z1)
									 tessellator.draw()
                                 
                                 GL11.glDepthMask(true)
                                 GL11.glEnable(3553)
                                 GL11.glDisable(3042)
                                 GL11.glEnable(3008)
                              

                              GL11.glBlendFunc(770, 771)
                              var82.updateFog()
                              GL11.glEnable(3553)
                              GL11.glEnable(3042)
                              new TextureLocation("/water.png").bindTexture()
                              GL11.glCallList(var89.listId + 1)
                              GL11.glDisable(3042)
                              GL11.glEnable(3042)
                              GL11.glColorMask(false, false, false, false)
                              var120 = var89.sortChunks(var126, 1)
                              GL11.glColorMask(true, true, true, true)
                              if var82.minecraft.settings.anaglyph:
                                 if var77 == 0:
                                    GL11.glColorMask(false, true, true, false)
                                  else:
                                    GL11.glColorMask(true, false, false, false)
                                 
                              

                              if var120 > 0:
                            	 new TextureLocation("/terrain.png").bindTexture()
                                 GL11.glCallLists(var89.buffer)
                              

                              GL11.glDepthMask(true)
                              GL11.glDisable(3042)
                              GL11.glDisable(2912)
                              if var82.minecraft.raining:
                                 float var97 = var80
                                 var27 = var82
                                 var28 = var82.minecraft.player
                                 Level var109 = var82.minecraft.level
                                 var104 = (int)var28.x
                                 var108 = (int)var28.y
                                 var114 = (int)var28.z
                                 GL11.glDisable(2884)
                                 GL11.glNormal3f(0.0F, 1.0F, 0.0F)
                                 GL11.glEnable(3042)
                                 GL11.glBlendFunc(770, 771)
                                 new TextureLocation("/rain.png").bindTexture()

                                 for(var110 = var104 - 5 var110 <= var104 + 5 ++var110) 
                                    for(var122 = var114 - 5 var122 <= var114 + 5 ++var122) 
                                       var120 = var109.getHighestTile(var110, var122)
                                       var86 = var108 - 5
                                       var125 = var108 + 5
                                       if var86 < var120:
                                          var86 = var120
                                       

                                       if var125 < var120:
                                          var125 = var120
                                       

                                       if var86 != var125:
                                          var74 = ((float)((var27.levelTicks + var110 * 3121 + var122 * 418711) % 32) + var97) / 32.0F
                                          float var124 = (float)var110 + 0.5F - var28.x
                                          var35 = (float)var122 + 0.5F - var28.z
                                          float var92 = MathHelper.sqrt(var124 * var124 + var35 * var35) / (float)5
                                          GL11.glColor4f(1.0F, 1.0F, 1.0F, (1.0F - var92 * var92) * 0.7F)
                                          tessellator.startDrawing(7)
                                          tessellator.addVertexWithUV((float)var110, (float)var86, (float)var122, 0.0F, (float)var86 * 2.0F / 8.0F + var74 * 2.0F)
                                          tessellator.addVertexWithUV((float)(var110 + 1), (float)var86, (float)(var122 + 1), 2.0F, (float)var86 * 2.0F / 8.0F + var74 * 2.0F)
                                          tessellator.addVertexWithUV((float)(var110 + 1), (float)var125, (float)(var122 + 1), 2.0F, (float)var125 * 2.0F / 8.0F + var74 * 2.0F)
                                          tessellator.addVertexWithUV((float)var110, (float)var125, (float)var122, 0.0F, (float)var125 * 2.0F / 8.0F + var74 * 2.0F)
                                          tessellator.addVertexWithUV((float)var110, (float)var86, (float)(var122 + 1), 0.0F, (float)var86 * 2.0F / 8.0F + var74 * 2.0F)
                                          tessellator.addVertexWithUV((float)(var110 + 1), (float)var86, (float)var122, 2.0F, (float)var86 * 2.0F / 8.0F + var74 * 2.0F)
                                          tessellator.addVertexWithUV((float)(var110 + 1), (float)var125, (float)var122, 2.0F, (float)var125 * 2.0F / 8.0F + var74 * 2.0F)
                                          tessellator.addVertexWithUV((float)var110, (float)var125, (float)(var122 + 1), 0.0F, (float)var125 * 2.0F / 8.0F + var74 * 2.0F)
                                          tessellator.draw()
                                       
                                    
                                 

                                 GL11.glEnable(2884)
                                 GL11.glDisable(3042)
                              

                              if var82.entity != null:
                                 var82.entity.renderHover(var80)
                              

                              GL11.glClear(256)
                              GL11.glLoadIdentity()
                              if var82.minecraft.settings.anaglyph:
                                 GL11.glTranslatef((float)((var77 << 1) - 1) * 0.1F, 0.0F, 0.0F)
                              

                              var82.hurtEffect(var80)
                              if var82.minecraft.settings.viewBobbing:
                                 var82.applyBobbing(var80)
                              

                              HeldBlock var112 = var82.heldBlock
                              var117 = var82.heldBlock.lastPos + (var112.pos - var112.lastPos) * var80
                              var116 = var112.minecraft.player
                              GL11.glPushMatrix()
                              GL11.glRotatef(var116.xRotO + (var116.xRot - var116.xRotO) * var80, 1.0F, 0.0F, 0.0F)
                              GL11.glRotatef(var116.yRotO + (var116.yRot - var116.yRotO) * var80, 0.0F, 1.0F, 0.0F)
                              var112.minecraft.renderer.setLighting(true)
                              GL11.glPopMatrix()
                              GL11.glPushMatrix()
                              var69 = 0.8F
                              if var112.moving:
                                 var33 = MathHelper.sin((var74 = ((float)var112.offset + var80) / 7.0F) * 3.1415927F)
                                 GL11.glTranslatef(-MathHelper.sin(MathHelper.sqrt(var74) * 3.1415927F) * 0.4F, MathHelper.sin(MathHelper.sqrt(var74) * 3.1415927F * 2.0F) * 0.2F, -var33 * 0.2F)
                              

                              GL11.glTranslatef(0.7F * var69, -0.65F * var69 - (1.0F - var117) * 0.6F, -0.9F * var69)
                              GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F)
                              GL11.glEnable(2977)
                              if var112.moving:
                                 var33 = MathHelper.sin((var74 = ((float)var112.offset + var80) / 7.0F) * var74 * 3.1415927F)
                                 GL11.glRotatef(MathHelper.sin(MathHelper.sqrt(var74) * 3.1415927F) * 80.0F, 0.0F, 1.0F, 0.0F)
                                 GL11.glRotatef(-var33 * 20.0F, 1.0F, 0.0F, 0.0F)
                              

                              GL11.glColor4f(var74 = var112.minecraft.level.getBrightness((int)var116.x, (int)var116.y, (int)var116.z), var74, var74, 1.0F)
                              if var112.block != null:
                                 var34 = 0.4F
                                 GL11.glScalef(0.4F, var34, var34)
                                 GL11.glTranslatef(-0.5F, -0.5F, -0.5F)
                                 new TextureLocation("/terrain.png").bindTexture()
                                 var112.block.renderPreview()
                               else:
                                 var116.bindTexture()
                                 GL11.glScalef(1.0F, -1.0F, -1.0F)
                                 GL11.glTranslatef(0.0F, 0.2F, 0.0F)
                                 GL11.glRotatef(-120.0F, 0.0F, 0.0F, 1.0F)
                                 GL11.glScalef(1.0F, 1.0F, 1.0F)
                                 var34 = 0.0625F
                                 ModelPart var127 = var112.minecraft.player.getModel().leftArm
                                 if !(var127 = var112.minecraft.player.getModel().leftArm).compiled:
                                    var127.render(var34)
                                 

                                 GL11.glCallList(var127.displayList)
                              

                              GL11.glDisable(2977)
                              GL11.glPopMatrix()
                              var112.minecraft.renderer.setLighting(false)
                              if !var82.minecraft.settings.anaglyph:
                                 break
                              

                              ++var77
                           

                           var66.minecraft.hud.render(var65, var66.minecraft.currentScreen != null, var94, var70)
                         else:
                           GL11.glViewport(0, 0, var66.minecraft.width, var66.minecraft.height)
                           GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F)
                           GL11.glClear(16640)
                           GL11.glMatrixMode(5889)
                           GL11.glLoadIdentity()
                           GL11.glMatrixMode(5888)
                           GL11.glLoadIdentity()
                           var66.enableGuiMode()
                        

                        if var66.minecraft.currentScreen != null:
                           var66.minecraft.currentScreen.render(var94, var70)
                        

                        Thread.yield()
                        GL11.updateDisplay()
                     
                  

                  if this.settings.limitFramerate:
                     try 
						Thread.sleep(5L)
					 catch (InterruptedException e) 
						e.printStackTrace()
					
                  

               checkGLError("Post render")
               ++var15

               while System.currentTimeMillis() >= var13 + 1000L:
                  this.debug = var15 + " fps, " + Chunk.chunkUpdates + " chunk updates"
                  Chunk.chunkUpdates = 0
                  var13 += 1000L
                  var15 = 0
               
            
         
   

   public final void grabMouse() 
	   if !GL11.isFocused():
		   return
	   
	   if GL11.isPointerLocked2():
			return
	    else:
		   GL11.mouseSetGrabbed(true)
		   //this.setCurrentScreen((GuiScreen)null)
	       this.lastClick = this.ticks + 10000
	       return
	   
   
   
   public final void ungrabMouse() 
	   if GL11.isPointerLocked2():
			GL11.mouseSetGrabbed(false)
	    else:
		   GL11.mouseSetGrabbed(false)
		   //this.setCurrentScreen((GuiScreen)null)
	       //this.lastClick = this.ticks + 10000
	       return
	   
   

   public final void pause() 
      if this.currentScreen == null:
         this.setCurrentScreen(new PauseScreen())
      
   

   # Private - Not needed in Pythonvoid onMouseClick(int var1) 
      if var1 != 0 || this.blockHitTime <= 0:
         HeldBlock var2
         if var1 == 0:
            var2 = this.renderer.heldBlock
            this.renderer.heldBlock.offset = -1
            var2.moving = true
         

         int var3
         if var1 == 1 && (var3 = this.player.inventory.getSelected()) > 0 && this.gamemode.useItem(this.player, var3):
            var2 = this.renderer.heldBlock
            this.renderer.heldBlock.pos = 0.0F
          else if this.selected == null:
            if var1 == 0 && !(this.gamemode instanceof CreativeGameMode):
               this.blockHitTime = 10
            

          else:
            if this.selected.entityPos == 1:
               if var1 == 0:
                  this.selected.entity.hurt(this.player, 4)
                  return
               
             else if this.selected.entityPos == 0:
               var3 = this.selected.x
               int var4 = this.selected.y
               int var5 = this.selected.z
               if var1 != 0:
                  if this.selected.face == 0:
                     --var4
                  

                  if this.selected.face == 1:
                     ++var4
                  

                  if this.selected.face == 2:
                     --var5
                  

                  if this.selected.face == 3:
                     ++var5
                  

                  if this.selected.face == 4:
                     --var3
                  

                  if this.selected.face == 5:
                     ++var3
                  
               

               Block var6 = Block.blocks[this.level.getTile(var3, var4, var5)]
               if var1 == 0:
                  if var6 != Block.BEDROCK || this.player.userType >= 100:
                     this.gamemode.hitBlock(var3, var4, var5)
                     return
                  
                else:
                  int var10
                  if (var10 = this.player.inventory.getSelected()) <= 0:
                     return
                  

                  Block var8
                  AABB var9
                  if ((var8 = Block.blocks[this.level.getTile(var3, var4, var5)]) == null || var8 == Block.WATER || var8 == Block.STATIONARY_WATER || var8 == Block.LAVA || var8 == Block.STATIONARY_LAVA) && ((var9 = Block.blocks[var10].getCollisionBox(var3, var4, var5)) == null || (this.player.bb.intersects(var9)?false:this.level.isFree(var9))):
                     if !this.gamemode.canPlace(var10):
                        return
                     
                     
                     this.level.netSetTile(var3, var4, var5, var10)
                     var2 = this.renderer.heldBlock
                     this.renderer.heldBlock.pos = 0.0F
                     Block.blocks[var10].onPlace(this.level, var3, var4, var5)
                  
               
            

         
      
   

   public static bool hasBeenInitialized = false
   
   def tick(self):
    return None
