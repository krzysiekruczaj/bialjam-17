package com.ownedoutcomes.entity

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.ownedoutcomes.fieldWidth
import com.ownedoutcomes.halfScreenWidth
import ktx.box2d.body
import ktx.math.vec2

class Castle(image: Drawable, world: World, val spawnCenter: Vector2 = vec2(0f, fieldWidth.toFloat() / 2)) : AbstractEntity(world, image) {
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

        userData = this@CastleFacade
        density = 0.5f
        friction = 0.3f
        restitution = 0.1f
      }
    }

  override fun update(delta: Float) {
    setPosition(body.position.x + halfScreenWidth, body.position.y + halfScreenWidth)
  }
}
