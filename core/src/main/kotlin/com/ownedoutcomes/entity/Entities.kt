package com.ownedoutcomes.entity

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.ownedoutcomes.fieldWidth
import com.ownedoutcomes.screenHeight
import com.ownedoutcomes.screenWidth
import com.ownedoutcomes.view.enemyCollisionGroup
import com.ownedoutcomes.view.enemyGroup
import ktx.box2d.body
import ktx.math.vec2

abstract class AbstractEntity(val world: World) {
  lateinit var body: Body
  abstract var size: Float
  abstract var angle: Float
  abstract var life: Float

  fun initiate(): AbstractEntity {
    body = createBody(world)
    return this
  }

  abstract fun createBody(world: World): Body

  abstract fun update(delta: Float)
}

class Chicken(world: World, override var life: Float, val level: Int) : Enemy(world, life)

abstract class Enemy(world: World,
                     override var life: Float,
                     val destination: Vector2 = vec2(0f, fieldWidth.toFloat() / 2)) : AbstractEntity(world) {
  override var size = 20f
  override var angle = 0f

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

      circle(size) {
        userData = this@Enemy
        density = 0.05f
        friction = 0.2f
        restitution = 0.1f
        filter.categoryBits = enemyGroup
        filter.maskBits = enemyCollisionGroup
      }

    }

  override fun update(delta: Float) {
    val currentDensity = 15f + size * MathUtils.PI * 2000f * 1.05f
    val bodyPosition = this.body.position
    angle = MathUtils.atan2(destination.y - bodyPosition.y, destination.x - bodyPosition.x)
    body.applyForceToCenter(
      MathUtils.cos(angle) * currentDensity,
      MathUtils.sin(angle) * currentDensity,
      true)
  }
}
