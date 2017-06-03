package com.ownedoutcomes.entity

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.ownedoutcomes.*
import ktx.box2d.body
import ktx.math.vec2
import java.util.*
import java.util.concurrent.ThreadLocalRandom

val playerDensity = 20f

class Castle(image: Drawable, world: World, val spawnCenter: Vector2) : AbstractEntity(world, image) {
  init {
    initiate()
  }

  override fun createBody(world: World) =
    world.body {
      type = BodyDef.BodyType.StaticBody
      fixedRotation = true
      linearDamping = 1f
      position.x = spawnCenter.x
      position.y = spawnCenter.y
      circle(25f) {
        userData = this@Castle
        density = 0.5f
        friction = 0.3f
        restitution = 0.1f
      }
    }

  override fun update(delta: Float) {
    setPosition(body.position.x + halfScreenWidth, body.position.y + halfScreenWidth)
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

class Chicken(image: Drawable, world: World, life: Float) : Enemy(image, world, life = life)

abstract class Enemy(image: Drawable, world: World, life: Float) : AbstractEntity(world, image) {
  val destination = vec2(600f, 600f)
  val size = 1f
  var angle = 0f
  val rand = Random()

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

class CastleFacade(image: Drawable, world: World, life: Float, val spawnVector: Vector2) : AbstractEntity(world, image) {

  init {
    initiate()
  }

  override fun createBody(world: World) =
    world.body {
      type = BodyDef.BodyType.StaticBody
      fixedRotation = true
      linearDamping = 1f
      position.x = spawnVector.x
      position.y = spawnVector.y

      circle(25f) {
        width = 50f
        height = 50f

        userData = this
        density = 0.5f
        friction = 0.3f
        restitution = 0.1f
      }
    }

  override fun update(delta: Float) {
    setPosition(body.position.x + halfScreenWidth, body.position.y + halfScreenWidth)
  }
}

class Skurwysyn(image: Drawable, world: World, life: Float, val spawnCenter: Vector2) : Enemy(image, world, life = life) {
  var speedBonus = random(0.8f, 1.5f)

  override fun createBody(world: World) =
    world.body {
      type = BodyDef.BodyType.DynamicBody
      fixedRotation = true
      linearDamping = 1f
//      position.x = random(halfScreenHeight.toFloat(), -halfScreenWidth.toFloat())
//      position.y = random(halfScreenHeight.toFloat(), halfScreenWidth.toFloat())

      position.x = -halfScreenWidth.toFloat() + 100f
      position.y = halfScreenHeight.toFloat() - 100f
      rotation = -30f


//      x = halfScreenWidth.toFloat() - (3 * fieldWidth.toFloat() / 2)
//      y = halfScreenHeight.toFloat()- fieldHeight.toFloat()
      circle(25f) {
        width = 50f
        height = 50f

        userData = this
        density = 0.5f
        friction = 0.3f
        restitution = 0.1f
      }
    }

  override fun update(delta: Float) {
    val currentDensity = size * size * MathUtils.PI * playerDensity * speedBonus * random(0.9f, 1.1f)
    body.applyForceToCenter(
      body.linearVelocity.x,
      body.linearVelocity.y,
      true)

    body.applyForceToCenter(currentDensity, 0f, true)
    setPosition(body.position.x + halfScreenWidth, body.position.y + halfScreenWidth)
    rotateBy(angle)
  }

}

fun random(from: Float, to: Float) = from + ThreadLocalRandom.current().nextFloat() * (to - from)
