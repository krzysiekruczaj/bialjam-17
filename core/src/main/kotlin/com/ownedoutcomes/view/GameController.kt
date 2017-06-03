package com.ownedoutcomes.view

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Array
import com.ownedoutcomes.entity.Castle
import com.ownedoutcomes.entity.Chicken
import com.ownedoutcomes.entity.Enemy
import ktx.collections.gdxSetOf
import ktx.collections.isNotEmpty
import ktx.math.vec2

class GameController(val skin: Skin) {
  val world = World(vec2(0f, 0f), true)
  val castle = Castle(skin.getDrawable("flag_blue"), world, 1000f)
  val enemies: MutableList<Chicken> = mutableListOf()
  val enemiesToRemove = gdxSetOf<Enemy>()

  init {
    world.setContactListener(ContactController(this))
  }

  fun removeEnemies() {
    if (enemiesToRemove.isNotEmpty()) {
      enemiesToRemove.forEach {
        val bd = Array<Body>()
        world.getBodies(bd)

        if (bd.contains(it.body, true)) {
          it.body.isActive = false
          world.destroyBody(it.body)
        }

        enemies.remove(it)
      }
    }
  }

  fun spawnEnemies() =
    (0..200).map {
      val chicken = Chicken(skin.getDrawable("chicken${MathUtils.random.nextInt(4) + 4}_v1"), world, 1f)
      enemies.add(chicken)
      chicken
    }
}
