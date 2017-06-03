package com.ownedoutcomes.entity

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.ownedoutcomes.halfScreenWidth
import com.ownedoutcomes.screenHeight
import com.ownedoutcomes.screenWidth
import ktx.box2d.body
import ktx.math.vec2

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

abstract class Enemy(image: Drawable, world: World, life: Float) : AbstractEntity(world, image) {
  val destination = vec2(600f, 600f)
  val size = 1f
  var angle = 0f

  var reachDestination = false

  init {
    initiate()
  }

  override fun createBody(world: World) =
    world.body {
      type = BodyDef.BodyType.DynamicBody
      fixedRotation = true
      linearDamping = 1f

      val newPosition = when (MathUtils.random.nextInt(4)) {
        0 -> vec2(-screenWidth - (MathUtils.random.nextFloat() * 100f), (MathUtils.random.nextFloat() * 2 * screenHeight) - screenHeight)
        1 -> vec2(screenWidth + (MathUtils.random.nextFloat() * 100f), (MathUtils.random.nextFloat() * 2 * -screenHeight) + screenHeight)
        2 -> vec2((MathUtils.random.nextFloat() * 2 * screenWidth) - screenWidth, -screenHeight - (MathUtils.random.nextFloat() * 100f))
        else -> vec2((MathUtils.random.nextFloat() * 2 * screenWidth) - screenWidth, screenHeight + (MathUtils.random.nextFloat() * 100f))
      }

      position.x = newPosition.x
      position.y = newPosition.y

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
