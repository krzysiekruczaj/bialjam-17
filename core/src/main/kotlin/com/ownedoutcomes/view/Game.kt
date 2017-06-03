package com.ownedoutcomes.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.ownedoutcomes.*
import com.ownedoutcomes.entity.Castle
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.box2d.body
import ktx.math.vec2
import ktx.scene2d.*


class Game(val stage: Stage, val skin: Skin, val world: World) : KtxScreen {
  val debugRenderer = Box2DDebugRenderer()
  val camera = OrthographicCamera(screenWidth.toFloat(), screenHeight.toFloat())
  val enemies: MutableList<Chicken> = mutableListOf()
  val castles: MutableList<Castle> = mutableListOf()

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
          createTileStyle(x, y, dirtStart, dirtEnd)
        }
        row()
      }
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
            val actorName = actor.name
            println("Button clicked. Event: $event, actor: $actorName")
          }
        }
      }
    }.cell(growX = false, height = 90f, pad = 10f)
    pack()
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
    }.cell(height = fieldHeight.toFloat(), width = fieldWidth.toFloat())
  }

  private fun KTableWidget.createImage(style: String) {
    image(style) {
    }.cell(height = fieldHeight.toFloat(), width = fieldWidth.toFloat())
  }

  override fun show() {
    reset()
    enemies.add(Chicken(skin.getDrawable("tower1"), world, 1f))
    castles.add(Castle(skin.getDrawable("castle_grey"), world, 100f))
    stage.addActor(view)
    enemies.onEach { stage.addActor(it) }
    castles.onEach { stage.addActor(it) }
    Gdx.input.inputProcessor = stage
  }

  override fun render(delta: Float) {
    stage.act(delta)
    enemies.onEach { it.update(delta) }
    castles.onEach { it.update(delta) }
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
