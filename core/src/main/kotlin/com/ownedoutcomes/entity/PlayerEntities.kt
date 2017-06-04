package com.ownedoutcomes.entity

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.ownedoutcomes.fieldWidth
import com.ownedoutcomes.view.*
import ktx.box2d.body
import ktx.collections.GdxSet
import ktx.collections.gdxSetOf
import ktx.math.vec2

class Castle(world: World, override var life: Float, val spawnCenter: Vector2 = vec2(0f, fieldWidth.toFloat() / 2)) : AbstractEntity(world) {
  override var size: Float = 75f
  override var angle = 0f
  var maxLife = life

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
      circle(size) {
        userData = this@Castle
        density = 0.5f
        friction = 0.3f
        restitution = 0.1f
        filter.categoryBits = towerGroup
        filter.maskBits = towerCollisionGroup
      }
    }

  override fun update(delta: Float) {
  }
}

class TowerFactory(val world: World) {
  fun wallTower(spawnVector: Vector2) = Tower(world, 1000f, spawnVector)
  fun fastTower(spawnVector: Vector2) = FastTower(world, 3000f, spawnVector)
  fun tripleShotFastTower(spawnVector: Vector2) = TripleShotFastTower(world, 3000f, spawnVector)
  fun splashTower(spawnVector: Vector2) = Tower(world, 3000f, spawnVector)
}

class TripleShotFastTower(world: World,
                          life: Float,
                          spawnVector: Vector2) : FastTower(world, life, spawnVector) {
  override fun shot(destination: Vector2): GdxSet<Bullet> {
    val bodyPosition = this.body.position
    angle = destination.angle(bodyPosition)
    return gdxSetOf(
      Bullet(world, 1f, bodyPosition, destination),
      Bullet(world, 1f, bodyPosition, destination.cpy().rotate(15f)),
      Bullet(world, 1f, bodyPosition, destination.cpy().rotate(-15f))
    )
  }

}

open class FastTower(world: World,
                override var life: Float,
                val spawnVector: Vector2) : AbstractEntity(world) {
  override var size: Float = 25f
  override var angle = 0f

  var lastShotTime = 0f
  var maxLife = life

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

      circle(size) {
        userData = this@FastTower
        density = 0.5f
        friction = 0.3f
        restitution = 0.1f
        filter.categoryBits = towerGroup
        filter.maskBits = towerCollisionGroup
      }
    }

  open fun shot(destination: Vector2): GdxSet<Bullet> {
    val bodyPosition = this.body.position
    angle = destination.angle(bodyPosition)
    return gdxSetOf(Bullet(world, 1f, bodyPosition, destination))
  }

  override fun update(delta: Float) {
    lastShotTime += delta
  }

}

class Bullet(world: World,
             override var life: Float,
             val spawnVector: Vector2,
             val destination: Vector2) : AbstractEntity(world) {
  override var size: Float = 5f
  override var angle = 0f

  var timeToLive = 0f

  init {
    initiate()
  }

  override fun createBody(world: World): Body =
    world.body {
      type = BodyDef.BodyType.DynamicBody
      fixedRotation = true
      linearDamping = 1f
      position.x = spawnVector.x
      position.y = spawnVector.y

      circle(2f) {
        userData = this@Bullet
        density = 0.05f
        friction = 0.3f
        restitution = 0.1f
        filter.categoryBits = bulletGroup
        filter.maskBits = bulletCollisionGroup
      }
    }

  override fun update(delta: Float) {
    timeToLive += delta
    val currentDensity = 15f * MathUtils.PI * 2000f * 1.05f * 1000f
    val angle = MathUtils.atan2(destination.y - body.position.y, destination.x - body.position.x)
    body.applyForce(
      MathUtils.cos(angle) * currentDensity,
      MathUtils.sin(angle) * currentDensity,
      destination.x,
      destination.y,
      true)
  }

  fun isTimeToLiveLimitExceeded(): Boolean {
    return timeToLive > 10f
  }
}

class Tower(world: World, override var life: Float, val spawnVector: Vector2) : AbstractEntity(world) {
  override var size: Float = 25f
  override var angle = 0f
  var maxLife = life

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

      circle(size) {
        userData = this@Tower
        density = 0.5f
        friction = 0.3f
        restitution = 0.1f
        filter.categoryBits = towerGroup
        filter.maskBits = towerCollisionGroup
      }
    }

  override fun update(delta: Float) {
  }
}
