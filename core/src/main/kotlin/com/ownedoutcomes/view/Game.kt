package com.ownedoutcomes.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.ownedoutcomes.*
import ktx.app.KtxScreen
import ktx.box2d.body
import ktx.math.vec2
import ktx.scene2d.buttonGroup
import ktx.scene2d.imageButton
import ktx.scene2d.table


class Game(val stage: Stage, val skin: Skin, val world: World) : KtxScreen {
  val debugRenderer = Box2DDebugRenderer()
  val camera = OrthographicCamera(screenWidth.toFloat(), screenHeight.toFloat())
  val enemies: MutableList<Chicken> = mutableListOf()

  val view = table {
    setFillParent(true)
    align(Align.bottomLeft)
    // Game:
    val bottomPadding = -110f
    table {
      val tilesY = (stageHeight.toInt() / fieldHeight)
      val tilesX = (stageWidth.toInt() / fieldWidth)

      val centerX = tilesX / 2

      val fieldRadius = 3
      val dirtStart = centerX - fieldRadius
      val dirtEnd = centerX + fieldRadius
      for (y in 0..tilesY - 1) {
        for (x in 0..tilesX - 1) {
          imageButton(style = createStyle(x, y, dirtStart, dirtEnd)) {

          }.cell(height = fieldHeight.toFloat(), width = fieldWidth.toFloat())
        }
        row()
      }
    }.cell(width = stageWidth, height = stageHeight, padBottom = bottomPadding, row = true)

    // GUI:
    buttonGroup(minCheckedCount = 0, maxCheckedCount = 1) {
      background("brown")
      repeat(5) { index ->
        imageButton(style = "tower$index") {
          it.height(60f).width(70f)
            .padBottom(5f).padTop(5f).padLeft(10f).padRight(10f)
        }
      }
    }.cell(growX = false, height = 90f, pad = 10f)
    pack()
  }

  private fun createStyle(x: Int, y: Int, dirtStart: Int, dirtEnd: Int): String {
    when {
      x == dirtStart && y == dirtStart -> return "grass_nw"
      x == dirtEnd && y == dirtStart -> return "grass_ne"
      x in dirtStart..dirtEnd && y == dirtStart -> return "grass_n"
      x == dirtStart && y == dirtEnd -> return "grass_sw"
      x == dirtEnd && y == dirtEnd -> return "grass_se"
      x in dirtStart..dirtEnd && y == dirtEnd -> return "grass_s"
      x == dirtStart && y in dirtStart..dirtEnd -> return "grass_w"
      x == dirtEnd && y in dirtStart..dirtEnd -> return "grass_e"
      x in dirtStart..dirtEnd && y in dirtStart..dirtEnd -> return "dirt"
      else -> return "grass"
    }
  }

  override fun show() {
    reset()
    enemies.add(Chicken(skin.getDrawable("tower1"), world, 1f))
    stage.addActor(view)
    enemies.onEach { stage.addActor(it) }
    Gdx.input.inputProcessor = stage
  }

  override fun render(delta: Float) {
    stage.act(delta)
    enemies.onEach { it.update(delta) }
    world.step(delta, 8, 3)
    stage.draw()
    debugRenderer.render(world, camera.combined)
  }

  fun reset() {}
}

class Chicken(image: Drawable, world: World, life: Float) : Enemy(image, world, life = life)

abstract class Enemy(image: Drawable,
                     world: World,
                     life: Float,
                     scaleX: Float = 40f,
                     scaleY: Float = 40f) : AbstractEntity(world, image) {
  private val destination = vec2(600f, 600f)
  private val size = 1f
  private val density = 0.5f
  private var angle = 0f

  init {
    initiate()
  }

  override fun createBody(world: World) =
    world.body {
      type = BodyDef.BodyType.DynamicBody
      fixedRotation = true
      linearDamping = 1f
      position.x = 100f - halfScreenWidth
      position.y = 100f - halfScreenHeight
      circle(20f) {
        userData = this
        density = 0.5f
        friction = 0.3f
        restitution = 0.1f
      }
    }

  override fun update(delta: Float) {
    val currentDensity = 15f + size * size * MathUtils.PI * 20f * 1.05f
    body.applyForceToCenter(
      -body.linearVelocity.x * currentDensity / 4f,
      -body.linearVelocity.y * currentDensity / 4f,
      true)
//    if (body.fixtureList.first().testPoint(inputController.x, inputController.y)) {//check if enemy touch defenece element
//      return
//    }
    angle = MathUtils.atan2(destination.y - body.position.y, destination.x - body.position.x)
    val xForce = MathUtils.cos(angle);
    val yForce = MathUtils.sin(angle);
    body.applyForceToCenter(
      xForce * currentDensity,
      yForce * currentDensity,
      true)

    setPosition(body.position.x + halfScreenWidth, body.position.y + halfScreenWidth)
    rotateBy(angle)
  }
}

abstract class AbstractEntity(val world: World, drawable: Drawable) : Image(drawable) {
  lateinit var body: Body

  fun initiate(): AbstractEntity {
    body = createBody(world)
    return this
  }

  abstract fun createBody(world: World): Body

  open fun update(delta: Float) {
    x = body.position.x
    y = body.position.y
  }
}
