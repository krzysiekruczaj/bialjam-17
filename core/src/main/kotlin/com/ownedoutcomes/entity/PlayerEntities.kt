package com.ownedoutcomes.entity

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.ownedoutcomes.fieldWidth
import ktx.box2d.body
import ktx.collections.gdxSetOf
import ktx.math.vec2

class Castle( world: World, val life: Float, val spawnCenter: Vector2 = vec2(0f, fieldWidth.toFloat() / 2)) : AbstractEntity(world) {
  var size: Float = 75f
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
      }
    }

  override fun update(delta: Float) {
  }
}

class TowerFactory(val skin: Skin, val world: World) {
  fun wallTower(spawnVector: Vector2) = Tower(skin.getDrawable("tower0"), world, 10f, spawnVector)
  fun fastTower(spawnVector: Vector2) = FastTower(skin.getDrawable("tower1"), skin.getDrawable("tower0"), world, 3f, spawnVector)
  fun splashTower(spawnVector: Vector2) = Tower(skin.getDrawable("tower2"), world, 3f, spawnVector)
}

class Tower(world: World, var life: Float, val spawnVector: Vector2) : AbstractEntity(world) {
  var size: Float = 25f
  var maxLife = life

class FastTower(towerImage: Drawable,
                val bulletImage: Drawable,
                world: World,
                var life: Float,
                val spawnVector: Vector2) : AbstractEntity(world, towerImage) {
  val bullets = gdxSetOf<Bullet>()
  var lastShotTime = 0f

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

        userData = this@FastTower
        density = 0.5f
        friction = 0.3f
        restitution = 0.1f
      }
    }

  fun shot(destination: Vector2) {
    bullets.add(Bullet(bulletImage, world, this.body.position, destination))
  }

  override fun update(delta: Float) {
    super.update(delta)
    bullets.onEach { it.update(delta) }
    lastShotTime += delta
  }

}

class Bullet(drawable: Drawable, world: World,
             val spawnVector: Vector2,
             val destination: Vector2) : AbstractEntity(world, drawable) {
  init {
    initiate()
  }

  override fun createBody(world: World): Body =
    world.body {
      type = BodyDef.BodyType.DynamicBody
      fixedRotation = true
      linearDamping = 1f
      position.x = spawnVector.x - 100f
      position.y = spawnVector.y - 100f

      circle(2f) {
        width = 50f
        height = 50f

        userData = this@Bullet
        density = 0.05f
        friction = 0.3f
        restitution = 0.1f
      }
    }

  override fun update(delta: Float) {
    val currentDensity = 15f * MathUtils.PI * 2000f * 1.05f
    val angle = MathUtils.atan2(destination.y - body.position.y, destination.x - body.position.x)
    println(body.fixtureList[0].userData)
    body.applyForceToCenter(
      MathUtils.cos(angle) * currentDensity,
      MathUtils.sin(angle) * currentDensity,
      true)

    setPosition(body.position.x + halfScreenWidth, body.position.y + halfScreenWidth)
  }
}

class Tower(image: Drawable, world: World, var life: Float, val spawnVector: Vector2) : AbstractEntity(world, image) {
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
      }
    }

  override fun update(delta: Float) {
  }
}
