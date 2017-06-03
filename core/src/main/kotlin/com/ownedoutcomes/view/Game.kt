package com.ownedoutcomes.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.ownedoutcomes.*
import com.ownedoutcomes.entity.Castle
import com.ownedoutcomes.entity.CastleFacade
import com.ownedoutcomes.entity.Chicken
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.math.vec2
import ktx.scene2d.*
import java.util.*


class Game(val stage: Stage, val skin: Skin, val world: World) : KtxScreen {
  val debugRenderer = Box2DDebugRenderer()
  val camera = OrthographicCamera(screenWidth.toFloat() * 3, screenHeight.toFloat() * 3)
  val enemies: MutableList<Chicken> = mutableListOf()
  val castles: MutableList<Castle> = mutableListOf()
  val castleFacades: MutableList<CastleFacade> = mutableListOf()

  val selectedFields: MutableSet<Actor> = mutableSetOf()

  var currentTower = 0

  val rand = Random()

  val view = table {
    setFillParent(true)
    align(Align.bottomLeft)

    // Game:
    val bottomPadding = -110f
    table {
      val tilesY = (stageHeight.toInt() / fieldHeight)
      val tilesX = (stageWidth.toInt() / fieldWidth)

      val centerX = tilesX / 2

      val fieldRadius = 5
      val dirtStart = centerX - fieldRadius
      val dirtEnd = centerX + fieldRadius
      for (y in 0..tilesY - 1) {
        for (x in 0..tilesX - 1) {
          createTileStyle(x, y, dirtStart, dirtEnd)
        }
        row()
      }

//      onKey { inputEvent: InputEvent, kTableWidget: KTableWidget, c: Char ->
//        run {
//          processKeyEvent(c)
//        }
//      }
    }.cell(width = stageWidth, height = stageHeight, padBottom = bottomPadding, row = true)

    // GUI:
    buttonGroup(minCheckedCount = 0, maxCheckedCount = 1) {
      background("brown")
      repeat(5) { index ->
        val buttonName = "tower$index"
        imageButton(style = buttonName) {
          name = buttonName
          it.height(60f).width(70f)
            .padBottom(5f).padTop(5f).padLeft(10f).padRight(10f)
          onClick { event: InputEvent, actor: KImageButton ->
            createCastleFacade(actor, event)
          }
        }
      }
    }.cell(growX = false, height = 90f, pad = 10f)
    pack()
  }

  private fun processKeyEvent(c: Char) {
    println("Key event: $c")
    when (c) {
      'q' -> println("No elo: Q")
      'w' -> println("No elo: W")
    }
  }

  private fun KImageButton.createCastleFacade(actor: KImageButton, event: InputEvent) {
    val actorName = actor.name
    println("Button clicked. Event: $event, actor: $actorName")
    selectedFields.forEach { actor: Actor ->
      val coordinates = stage.stageToScreenCoordinates(vec2(actor.x, actor.y))
      val x = coordinates.x
      val y = coordinates.y
      println("Creating CastleFacade at [$x, $y]")
      val castleFacade = CastleFacade(skin.getDrawable("chicken2_v1"), world, 100f, coordinates)
      castleFacades.add(castleFacade)
      stage.addActor(castleFacade)
    }
  }

  private fun KTableWidget.createTileStyle(x: Int, y: Int, dirtStart: Int, dirtEnd: Int): Unit {
    when {
      x == dirtStart && y == dirtStart -> return createImageButton("grass_nw")
      x == dirtEnd && y == dirtStart -> return createImageButton("grass_ne")
      x in dirtStart..dirtEnd && y == dirtStart -> return createImageButton("grass_n")
      x == dirtStart && y == dirtEnd -> return createImageButton("grass_sw")
      x == dirtEnd && y == dirtEnd -> return createImageButton("grass_se")
      x in dirtStart..dirtEnd && y == dirtEnd -> return createImageButton("grass_s")
      x == dirtStart && y in dirtStart..dirtEnd -> return createImageButton("grass_w")
      x == dirtEnd && y in dirtStart..dirtEnd -> return createImageButton("grass_e")
      x in dirtStart..dirtEnd && y in dirtStart..dirtEnd -> return createImageButton("dirt")
      else -> return createImage("grass")
    }
  }

  private fun KTableWidget.createImageButton(style: String): Unit {
    imageButton(style = style) {
      onClick { event: InputEvent, actor: KImageButton, x: Float, y: Float ->
        val actorX = actor.x
        val actorY = actor.y
        println("Clicked [$x, $y] with actor $actor on [$actorX, $actorY]")
        selectedFields.add(actor)
      }
    }.cell(height = fieldHeight.toFloat(), width = fieldWidth.toFloat())
  }

  private fun KTableWidget.createImage(style: String) {
    image(style) {
    }.cell(height = fieldHeight.toFloat(), width = fieldWidth.toFloat())
  }

  override fun show() {
    reset()
    for (i in 0..200) {
      enemies.add((Chicken(skin.getDrawable("chicken${rand.nextInt(4) + 4}_v1"), world, 1f)))
    }

    castles.add(Castle(skin.getDrawable("flag_blue"), world, vec2(0f, fieldWidth.toFloat() / 2)))
    stage.addActor(view)
    enemies.onEach { stage.addActor(it) }
    castles.onEach { stage.addActor(it) }
    castleFacades.onEach { stage.addActor(it) }
    Gdx.input.inputProcessor = stage
  }

  override fun render(delta: Float) {
    inputHandling()
    stage.act(delta)
    world.step(delta, 8, 3)
    enemies.onEach { it.update(delta) }
    castles.onEach { it.update(delta) }
    castleFacades.onEach { it.update(delta) }
    stage.draw()
    debugRenderer.render(world, camera.combined)
  }

  fun reset() {}

  fun inputHandling() {
    when {
      Gdx.input.isKeyPressed(Input.Keys.Q) -> currentTower = 0
      Gdx.input.isKeyPressed(Input.Keys.W) -> currentTower = 1
      Gdx.input.isKeyPressed(Input.Keys.E) -> currentTower = 2
      Gdx.input.isKeyPressed(Input.Keys.R) -> currentTower = 3
      Gdx.input.isKeyPressed(Input.Keys.T) -> currentTower = 4
    }
    println("current Tower:$currentTower")
  }
}
