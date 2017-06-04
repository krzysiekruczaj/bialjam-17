package com.ownedoutcomes.entity

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.ownedoutcomes.fieldWidth
import com.ownedoutcomes.view.bulletCollisionGroup
import com.ownedoutcomes.view.bulletGroup
import com.ownedoutcomes.view.towerCollisionGroup
import com.ownedoutcomes.view.towerGroup
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
  override fun createBullets(destination: Vector2): GdxSet<Bullet> {
    val source = createBulletSpawnVector()
    return gdxSetOf(
      createBullet(source, destination),
      createBullet(source, destination.cpy().rotate(15f)),
      createBullet(source, destination.cpy().rotate(-15f))
    )
  }


}

open class FastTower(world: World,
                     override var life: Float,
                     val spawnVector: Vector2) : AbstractEntity(world) {
  override var size: Float = 25f
  override var angle = 0f
  var shotDelay = 0.5f

  var lastShotTime = 0f
  var maxLife = life
  var towerPower = 1f

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

  fun shot(destination: Vector2): GdxSet<Bullet> {
    assignAngle(destination)
    return createBullets(destination)
  }

  private fun assignAngle(destination: Vector2) {
    val bodyPosition = this.body.position
    val towerY = bodyPosition.y
    val towerX = bodyPosition.x
    angle = MathUtils.atan2(destination.y - towerY, destination.x - towerX)
  }

  open fun createBullets(destination: Vector2): GdxSet<Bullet> {
    val bullet = createBullet(createBulletSpawnVector(), destination)
    return gdxSetOf(bullet)
  }

  protected fun createBullet(source: Vector2, destination: Vector2): Bullet {
    return Bullet(world, 1f, source, destination)
  }

  protected fun createBulletSpawnVector(): Vector2 {
    val bodyPosition = this.body.position
    val towerY = bodyPosition.y
    val towerX = bodyPosition.x
    val distanceX = MathUtils.cos(angle)
    val distanceY = MathUtils.sin(angle)
    val thunderX = towerX + distanceX * size
    val thunderY = towerY + distanceY * size
    val bulletSpawnVector = vec2(thunderX, thunderY)
    return bulletSpawnVector
  }

  override fun update(delta: Float) {
    lastShotTime += delta
  }

}

class Bullet(world: World,
             override var life: Float,
             val spawnVector: Vector2,
             val destination: Vector2,
             var power: Float = 1f) : AbstractEntity(world) {
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
