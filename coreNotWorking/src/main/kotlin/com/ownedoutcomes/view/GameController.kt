package com.ownedoutcomes.view

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.ownedoutcomes.entity.*
import ktx.collections.GdxSet
import ktx.collections.gdxSetOf
import ktx.collections.isNotEmpty
import ktx.math.vec2

class GameController(val skin: Skin) : Disposable {
  val world = World(vec2(0f, 0f), true)

  val castle = Castle(skin.getDrawable("flag_blue"), world, 1000f)

  val enemies = gdxSetOf<Chicken>()
  val enemiesToRemove = gdxSetOf<Chicken>()

  val towers = gdxSetOf<Tower>()
  val towersToRemove = gdxSetOf<Tower>()

  val fastTowers = gdxSetOf<FastTower>()
  val fastTowersToRemove = gdxSetOf<FastTower>()

  init {
    world.setContactListener(ContactController(this))
  }

  fun spawnEnemies() =
    (0..200).map {
      val chicken = Chicken(skin.getDrawable("chicken${MathUtils.random.nextInt(4) + 4}_v1"), world, 1f)
      enemies.add(chicken)
      chicken
    }

  override fun dispose() {
    world.dispose()
  }

  fun removeDestroyedGameObjects() {
    removeObjects(enemies, enemiesToRemove)
    removeObjects(towers, towersToRemove)
  }

  private fun <E : AbstractEntity> removeObjects(allObjects: GdxSet<E>, toRemove: GdxSet<E>) {
    if (toRemove.isNotEmpty()) {
      toRemove.forEach {
        val bd = Array<Body>()
        world.getBodies(bd)

        if (bd.contains(it.body, true)) {
          it.body.isActive = false
          world.destroyBody(it.body)
        }

        allObjects.remove(it)
      }
    }
    toRemove.clear()
  }

  fun update(delta: Float) {
    enemies.onEach { it.update(delta) }
    castle.update(delta)
    towers.onEach { it.update(delta) }

    fastTowers.onEach {
      it.update(delta)
      it.bullets.onEach { it.update(delta) }
      if (it.lastShotTime > 1f) {
        it.lastShotTime = 0f
        it.shot(vec2(100f, 100f))
      }
    }
  }
}