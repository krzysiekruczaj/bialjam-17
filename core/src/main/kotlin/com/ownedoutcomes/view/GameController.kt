package com.ownedoutcomes.view

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.ownedoutcomes.entity.*
import com.ownedoutcomes.fieldWidth
import com.ownedoutcomes.screenHeight
import com.ownedoutcomes.screenWidth
import ktx.collections.GdxSet
import ktx.collections.addAll
import ktx.collections.gdxSetOf
import ktx.collections.isNotEmpty
import ktx.math.vec2

class GameController : Disposable {
  val world = World(vec2(0f, 0f), true)

  val camera = OrthographicCamera(screenWidth.toFloat(), screenHeight.toFloat())

  val castle = Castle(world, 1000f)
  val enemies = gdxSetOf<Chicken>()
  val enemiesToRemove = gdxSetOf<Chicken>()

  val towers = gdxSetOf<Tower>()
  val towersToRemove = gdxSetOf<Tower>()

  val fastTowers = gdxSetOf<FastTower>()
  val fastTowersToRemove = gdxSetOf<FastTower>()

  val bullets = gdxSetOf<Bullet>()
  val bulletsToRemove = gdxSetOf<Bullet>()

  val fieldRadius = 5
  private val enemiesSpawnTimeout = 10f
  var lastSpawnDelta = 0.0f

  var points = 0

  init {
    world.setContactListener(ContactController(this))
  }

  fun spawnEnemies(enemiesCount: Int) =
    (0..enemiesCount).map {
      val chicken = Chicken(world, 1f)
      enemies.add(chicken)
      chicken
    }

  override fun dispose() {
    world.dispose()
  }

  fun removeDestroyedGameObjects() {
    removeObjects(enemies, enemiesToRemove)
    removeObjects(towers, towersToRemove)
    removeObjects(bullets, bulletsToRemove)
    removeObjects(fastTowers, fastTowersToRemove)
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
    world.step(delta, 8, 3)
    removeDestroyedGameObjects()

    if (lastSpawnDelta > enemiesSpawnTimeout) {
      lastSpawnDelta = 0.0f
      spawnEnemies(30)
    } else {
      lastSpawnDelta += delta
    }

    enemies.onEach { it.update(delta) }
    castle.update(delta)
    towers.onEach { it.update(delta) }

    bullets.onEach {
      it.update(delta)
      if (it.isTimeToLiveLimitExceeded()) {
        bulletsToRemove.add(it)
      }
    }

    val entitiesInRange = findEntitiesInRange(enemies)
    fastTowers.onEach {
      it.update(delta)
      if (it.lastShotTime > 0.5f) {
        it.lastShotTime = 0f
        val closestEntity = findClosestEntity(it, entitiesInRange)
        closestEntity?.let { closestEntity ->
          bullets.addAll(it.shot(closestEntity.body.position.cpy()))
        }
      }
    }
  }

  private fun findEntitiesInRange(enemies: GdxSet<Chicken>): GdxSet<Chicken> {
    val chickensInRange = gdxSetOf<Chicken>()

    chickensInRange.addAll(enemies.filter {
      castle.spawnCenter.dst(it.body.position) < (fieldRadius + 3) * 50f
    })

    return chickensInRange
  }

  private fun findClosestEntity(fastTower: FastTower, entitiesInRange: GdxSet<Chicken>): Enemy? {
    var minimumEntity: Enemy? = null
    var minimumDistance: Float = Float.MAX_VALUE
    entitiesInRange.onEach {
      val position = it.body.position
      val fastTowerPosition = fastTower.body.position
      val distance = vec2(position.x, position.y).dst(vec2(fastTowerPosition.x, fastTowerPosition.y))
      if (distance < 3 * fieldWidth && minimumDistance > distance) {
        minimumDistance = distance
        minimumEntity = it
      }
    }
    println("Minimum distance: $minimumDistance")
    return minimumEntity
  }

  fun gameOver() {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}
