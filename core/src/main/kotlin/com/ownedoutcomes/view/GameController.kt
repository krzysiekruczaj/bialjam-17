package com.ownedoutcomes.view

import com.badlogic.gdx.assets.AssetManager
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

class GameController(val assetManager: AssetManager) : Disposable {
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
  val enemiesSpawnTimeout = 10f
  var lastSpawnDelta = 0.0f

  var points: Int = 1000

  var currentWave = 0

  var nightDelay: Float = 10f

  var timeFromLastChangeOfTimeOfTheDay: Float = 0f
  private var factor: Int = 1

  init {
    world.setContactListener(ContactController(this, assetManager))
  }

  fun spawnEnemies(waveNumber: Int): List<Chicken> {
    val enemiesInWave = waves[waveNumber].chickens
      .flatMap {
        when (it.key) {
          0 -> (0..it.value).map { Chicken(world, 1f, 0) }
          1 -> (0..it.value).map { Chicken(world, 4f, 1) }
          2 -> (0..it.value).map { Chicken(world, 8f, 2) }
          3 -> (0..it.value).map { Chicken(world, 16f, 3) }
          4 -> (0..it.value).map { Chicken(world, 32f, 4) }
          5 -> (0..it.value).map { Chicken(world, 64f, 5) }
          6 -> (0..it.value).map { Chicken(world, 128f, 6) }
          7 -> (0..it.value).map { Chicken(world, 256f, 7) }
          8 -> (0..it.value).map { Chicken(world, 512f, 8) }
          9 -> (0..it.value).map { Chicken(world, 1024f, 9) }
          else -> emptyList()
        }
      }
    enemies.addAll(enemiesInWave)
    return enemiesInWave
  }

  override fun dispose() {
    world.dispose()
  }

  fun removeDestroyedGameObjects() {
    removeObjects(enemies, enemiesToRemove)
    removeObjects(towers, towersToRemove)
    removeObjects(fastTowers, fastTowersToRemove)
    filterBulletsThatFoundItsDestination(bullets, 5f)
    removeObjects(bullets, bulletsToRemove)
  }

  private fun filterBulletsThatFoundItsDestination(bullets: GdxSet<Bullet>, delta: Float) {
    bullets.onEach {
      val bulletPosition = it.body.position
      val destinationPosition = it.destination
      if (Math.abs(bulletPosition.x - destinationPosition.x) < delta && Math.abs(bulletPosition.y - destinationPosition.y) < delta) {
        bulletsToRemove.add(it)
      }
    }
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
      spawnEnemies(Math.min(currentWave++, 40))
    } else {
      lastSpawnDelta += delta
    }

    if (nightDelay < 0f) {
      if (timeFromLastChangeOfTimeOfTheDay > 10f) {
        factor = -1
      } else if (timeFromLastChangeOfTimeOfTheDay < 0f) {
        timeFromLastChangeOfTimeOfTheDay = 0f
        factor = 1
      }
      timeFromLastChangeOfTimeOfTheDay += delta * factor
    } else {
      nightDelay -= delta
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

    val entitiesInTowerRange = findEntitiesInCastleRange(entitiesInRange)

    fastTowers.onEach {
      it.update(delta)
      if (it.lastShotTime > it.shotDelay) {
        it.lastShotTime = 0f
        val closestEntityToCastle = findClosestEntity(it, entitiesInTowerRange)
        //println("Found ${entitiesInTowerRange.size} entities in castle range.")
        if (closestEntityToCastle != null) {
          //println("Closest entity to castle= [${closestEntityToCastle.body.position.x}, ${closestEntityToCastle.body.position.y}]")
          bullets.addAll(it.shot(closestEntityToCastle.body.position.cpy()))
        } else {
          val closestEntityToTower = findClosestEntity(it, entitiesInRange)
          closestEntityToTower?.let { closestEntity ->
            //println("Closest entity to tower = [${closestEntityToTower.body.position.x}, ${closestEntityToTower.body.position.y}]")

            bullets.addAll(it.shot(closestEntity.body.position.cpy().scl(1000f)))
          }
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

  private fun findEntitiesInCastleRange(enemies: GdxSet<Chicken>): GdxSet<Chicken> {
    val chickensInRange = gdxSetOf<Chicken>()

    chickensInRange.addAll(enemies.filter {
      castle.spawnCenter.dst(it.body.position) < (fieldRadius) * 50f
    })

    return chickensInRange
  }

  private fun findClosestEntity(fastTower: FastTower, entitiesInRange: GdxSet<Chicken>): Enemy? {
    var minimumEntity: Enemy? = null
    var minimumDistance: Float = Float.MAX_VALUE
    entitiesInRange.onEach {
      val chickenPosition = it.body.position
      val fastTowerPosition = fastTower.body.position
      val distance = vec2(chickenPosition.x, chickenPosition.y).dst(vec2(fastTowerPosition.x, fastTowerPosition.y))
      if (distance < 4 * fieldWidth && minimumDistance > distance) {
        minimumDistance = distance
        minimumEntity = it
      }
    }
    return minimumEntity
  }

  fun gameOver() {
    System.exit(-1)
    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}

val waves = (0..30).map {
  listOf(Wave(mapOf(it to 30, it + 1 to 3)),
    Wave(mapOf(it to 30, it + 1 to 10)),
    Wave(mapOf(it to 30, it + 1 to 20)),
    Wave(mapOf(it to 30, it + 1 to 30)))
}.flatMap { it }

data class Wave(
  val chickens: Map<Int, Int>
)

