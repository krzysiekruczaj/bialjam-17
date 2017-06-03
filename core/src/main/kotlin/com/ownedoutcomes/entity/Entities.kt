package com.ownedoutcomes.entity

import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.ownedoutcomes.fieldHeight
import com.ownedoutcomes.fieldWidth
import com.ownedoutcomes.halfScreenHeight
import com.ownedoutcomes.halfScreenWidth
import com.ownedoutcomes.view.Enemy
import ktx.box2d.body

class Castle(image: Drawable, world: World, life: Float) : Enemy(image, world, life = life) {
  init {
    initiate()
  }

  override fun createBody(world: World) =
    world.body {
      type = BodyDef.BodyType.StaticBody
      fixedRotation = true
      linearDamping = 1f
      x = halfScreenWidth.toFloat() - (3 * fieldWidth.toFloat() / 2)
      y = halfScreenHeight.toFloat()- fieldHeight.toFloat()
      box {
        width = 150f
        height = 150f

        userData = this
        density = 0.5f
        friction = 0.3f
        restitution = 0.1f
      }
    }

  override fun update(delta: Float) {
  }
}
