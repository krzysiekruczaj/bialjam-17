package com.ownedoutcomes.entity

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.ownedoutcomes.fieldWidth
import ktx.box2d.body
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

class TowerFactory(val world: World) {
  fun wallTower(spawnVector: Vector2) = Tower(world, 10f, spawnVector)
  fun fastTower(spawnVector: Vector2) = Tower(world, 3f, spawnVector)
  fun splashTower(spawnVector: Vector2) = Tower( world, 3f, spawnVector)
}

class Tower(world: World, var life: Float, val spawnVector: Vector2) : AbstractEntity(world) {
  var size: Float = 25f
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
      }
    }

  override fun update(delta: Float) {
  }
}
