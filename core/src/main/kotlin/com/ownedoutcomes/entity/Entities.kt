package com.ownedoutcomes.entity

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.ownedoutcomes.fieldHeight
import com.ownedoutcomes.fieldWidth
import com.ownedoutcomes.halfScreenHeight
import com.ownedoutcomes.halfScreenWidth
import ktx.box2d.body
import ktx.math.vec2

class Castle(image: Drawable, world: World) : AbstractEntity(world, image) {
  init {
    initiate()
  }

  override fun createBody(world: World) =
    world.body {
      type = BodyDef.BodyType.StaticBody
      fixedRotation = true
      linearDamping = 1f
      x = halfScreenWidth.toFloat() - (3 * fieldWidth.toFloat() / 2)
      y = halfScreenHeight.toFloat() - fieldHeight.toFloat()
      box {
        width = 150f
        height = 150f

        userData = this@Castle
        density = 0.5f
        friction = 0.3f
        restitution = 0.1f
      }
    }

  override fun update(delta: Float) {}
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

class Chicken(image: Drawable, world: World, life: Float) : Enemy(image, world, life = life)

abstract class Enemy(image: Drawable,
                     world: World,
                     life: Float) : AbstractEntity(world, image) {
  private val destination = vec2(600f, 600f)
  private val size = 1f
  private var angle = 0f
  var reachDestination = false

  init {
    initiate()
  }

  override fun createBody(world: World) =
    world.body {
      type = BodyDef.BodyType.DynamicBody
      fixedRotation = true
      linearDamping = 1f
      position.x = 300f - halfScreenWidth
      position.y = 300f - halfScreenHeight
      circle(20f) {
        userData = this@Enemy
        density = 0.05f
        friction = 0.2f
        restitution = 0.1f
      }
    }

  override fun update(delta: Float) {
    if (reachDestination) {
      body.applyForceToCenter(0f, 0f, false)
      return
    }

    val currentDensity = 15f + size * MathUtils.PI * 2000f * 1.05f
    angle = MathUtils.atan2(destination.y - body.position.y, destination.x - body.position.x)
    body.applyForceToCenter(
      MathUtils.cos(angle) * currentDensity,
      MathUtils.sin(angle) * currentDensity,
      true)

    setPosition(body.position.x + halfScreenWidth, body.position.y + halfScreenWidth)
  }
}

enum class EntityType {
  CASTLE, CHICKEN
}
