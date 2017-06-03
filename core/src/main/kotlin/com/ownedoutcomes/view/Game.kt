package com.ownedoutcomes.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.ownedoutcomes.*
import com.ownedoutcomes.entity.Castle
import com.ownedoutcomes.entity.CastleFacade
import com.ownedoutcomes.entity.Chicken
import ktx.actors.onClick
import ktx.actors.onKey
import ktx.app.KtxScreen
import ktx.math.vec2
import ktx.scene2d.*


class Game(val stage: Stage,
           val skin: Skin,
           val gameController: GameController) : KtxScreen {
  val debugRenderer = Box2DDebugRenderer()
  val camera = OrthographicCamera(screenWidth.toFloat(), screenHeight.toFloat())
  val castleFacades: MutableList<CastleFacade> = mutableListOf()

  private lateinit var towerTypes: KButtonTable
  private lateinit var pointsLabel: Label

  var currentTower = 0

  var lastSpawnDelta = 0.0f

  var points = 0

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

    }.cell(width = stageWidth, height = stageHeight, padBottom = bottomPadding, row = true)

    // GUI:
    towerTypes = buttonGroup(minCheckedCount = 0, maxCheckedCount = 1) {
      background("brown")
      repeat(5) { index ->
        val buttonName = "tower$index"
        imageButton(style = buttonName) {
          name = buttonName
          it.height(60f).width(70f)
            .padBottom(5f).padTop(5f).padLeft(10f).padRight(10f)
        }
      }

      // Points:
      pointsLabel = label(text = "$:$points") {
        cell ->
        run {
          cell.expand().align(Align.topRight)
        }
      }
    }
    towerTypes.cell(growX = false, height = 90f, pad = 10f)

    onKey { inputEvent: InputEvent, kTableWidget: KTableWidget, c: Char ->
      run {
        println("Pressed key = [$c]")
        when (c) {
          'q' -> setCastleFacadeType(0)
          'w' -> setCastleFacadeType(1)
          'e' -> setCastleFacadeType(2)
          'r' -> setCastleFacadeType(3)
          't' -> setCastleFacadeType(4)
        }
      }
    }

    pack()
  }

  private fun setCastleFacadeType(id: Int) {
    currentTower = id
    val buttonGroup = towerTypes.buttonGroup
    buttonGroup.checked?.isChecked = false
    buttonGroup.buttons[id].isChecked = true
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
      onClick { _: InputEvent, actor: KImageButton, x: Float, y: Float ->
        println("Clicked [$x, $y] with actor $actor on [${actor.x}, ${actor.y}]")

        println("Creating ${castleFacades.size} castle facades. Creating facade with id = [$currentTower]")

        val x = actor.getX(Align.center) - halfScreenWidth
        val y = actor.getY(Align.center) - halfScreenHeight

        println("Creating CastleFacade at [$x, $y]")

        val castleFacade = CastleFacade(
          skin.getDrawable("tower$currentTower"),
          gameController.world,
          100f,
          vec2(x, y)
        )

        castleFacades.add(castleFacade)
        stage.addActor(castleFacade)

        actor.isChecked = false
      }
    }.cell(height = fieldHeight.toFloat(), width = fieldWidth.toFloat())
  }

  private fun KTableWidget.createImage(style: String) {
    image(style) {
    }.cell(height = fieldHeight.toFloat(), width = fieldWidth.toFloat())
  }

  override fun show() {
    reset()
    stage.addActor(view)
    stage.addActor(gameController.castle)
    castleFacades.onEach { stage.addActor(it) }
    Gdx.input.inputProcessor = stage
    stage.keyboardFocus = view
  }

  private val enemiesSpawnTimeout = 10

  override fun render(delta: Float) {
    gameController.world.step(delta, 8, 3)
    gameController.removeEnemies()

    if (lastSpawnDelta > enemiesSpawnTimeout) {
      lastSpawnDelta = 0.0f
      gameController.spawnEnemies().onEach { stage.addActor(it) }
    } else {
      lastSpawnDelta += delta
    }

    updatePoints();

    gameController.enemies.onEach { it.update(delta) }
    gameController.castle.update(delta)
    castleFacades.onEach { it.update(delta) }
    stage.act(delta)
    stage.draw()
    debugRenderer.render(gameController.world, camera.combined)
  }

  private fun updatePoints() {
    points++
    pointsLabel.setText("Money: $points")
  }

  fun reset() {}
}
