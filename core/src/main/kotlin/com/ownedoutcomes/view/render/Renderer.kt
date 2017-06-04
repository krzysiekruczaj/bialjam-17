package com.ownedoutcomes.view.render

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.ownedoutcomes.entity.AbstractEntity
import com.ownedoutcomes.entity.FastTower
import com.ownedoutcomes.entity.Tower
import com.ownedoutcomes.entity.TripleShotFastTower
import com.ownedoutcomes.screenHeight
import com.ownedoutcomes.screenWidth
import com.ownedoutcomes.view.GameController


class GameRenderer(val gameController: GameController, val batch: Batch, skin: Skin) {
  private val castleSprite = skin.atlas.createSprite("castle_v1")
  private val towerSprite = skin.atlas.createSprite("tower")

  private val enemySprite = skin.atlas.createSprite("chicken2_v1")

  private val chickenSprites = arrayOf(
    skin.atlas.createSprite("chicken_v0"),
    skin.atlas.createSprite("chicken4_v0"),
    skin.atlas.createSprite("chicken1_v0"),
    skin.atlas.createSprite("chicken3_v1"),
    skin.atlas.createSprite("chicken2_v1"),
    skin.atlas.createSprite("chicken4_v1"),
    skin.atlas.createSprite("chicken5_v1"),
    skin.atlas.createSprite("chicken6_v1"),
    skin.atlas.createSprite("chicken7_v1")
  )

  private val fastTowerSprites = arrayOf(
    skin.atlas.createSprite("tower1"),
    skin.atlas.createSprite("tower2")
  )
  private val bulletSprite = skin.atlas.createSprite("bullet")

  private val healthSprite = skin.atlas.createSprite("health")
  private val enemyHealthSprite = skin.atlas.createSprite("health-enemy")

  val debugRenderer = Box2DDebugRenderer()


  init {
    castleSprite.setOriginCenter()
    enemySprite.setOriginCenter()

    chickenSprites.forEach {
      it.rotate90(true)
    }
  }

  fun render(delta: Float) {
    batch.projectionMatrix = gameController.camera.combined
    debugRenderer.render(gameController.world, gameController.camera.combined)

    batch.begin()
    renderCastle()
    renderEnemies(gameController.castle.spawnCenter)
    renderTowers()
    renderFastTowers()
    renderBullets()

    renderNight()

    batch.end()
  }

  private fun renderNight() {
    val pixmap = Pixmap(0, 10, Pixmap.Format.RGBA8888)
    pixmap.fill()
    val drawable = TextureRegion(Texture(pixmap))
    batch.setColor(0.5f, 0.5f, 0.5f, (gameController.timeFromLastChangeOfTimeOfTheDay / 10) * 0.7f)
    batch.draw(drawable, -screenWidth.toFloat() / 2, -screenHeight.toFloat() / 2, screenWidth.toFloat(), screenHeight.toFloat())
    pixmap.dispose()
  }

  private fun processSprite(sprite: Sprite, entity: AbstractEntity): Sprite {
    val bulletSprite = createSprite(sprite, entity)
    bulletSprite.rotation = MathUtils.radiansToDegrees * entity.angle
    bulletSprite.draw(batch)
    return bulletSprite
  }

  private fun createSprite(sprite: Sprite, entity: AbstractEntity): Sprite {
    val bulletSprite = Sprite(sprite)
    val spriteSize = entity.size * 2
    bulletSprite.x = entity.body.position.x - entity.size
    bulletSprite.y = entity.body.position.y - entity.size
    bulletSprite.setSize(spriteSize, spriteSize)
    bulletSprite.setOriginCenter()
    return bulletSprite
  }

  private fun renderBullets() {
    gameController.bullets.onEach {
      processSprite(bulletSprite, it)
    }
  }

  private fun renderCastle() {
    val castle = gameController.castle
    processSprite(castleSprite, castle)
    renderHealthSprite(castle)
  }

  private fun renderHealthSprite(castle: AbstractEntity) {
    val healthSprite = Sprite(healthSprite)
    val spriteSize = castle.size * 2 * (castle.life / castle.maxLife)
    healthSprite.x = castle.body.position.x - castle.size
    healthSprite.y = castle.body.position.y + castle.size
    healthSprite.setSize(spriteSize, 10f)
    healthSprite.setOriginCenter()
    healthSprite.draw(batch)
  }

  private fun renderEnemyHealthSprite(enemy: AbstractEntity) {
    val healthSprite = Sprite(enemyHealthSprite)
    val spriteSize = enemy.size * 2 * (enemy.life / enemy.maxLife)
    healthSprite.x = enemy.body.position.x - enemy.size
    healthSprite.y = enemy.body.position.y + enemy.size
    healthSprite.setSize(spriteSize, 10f)
    healthSprite.setOriginCenter()
    healthSprite.draw(batch)
  }

  private fun renderEnemies(playerPosition: Vector2) {
    gameController.enemies.onEach {
      val bulletSprite = createSprite(chickenSprites[Math.min(it.level, 8)], it)
      bulletSprite.rotation = MathUtils.radiansToDegrees * it.angle
      bulletSprite.draw(batch)
      renderEnemyHealthSprite(it)
    }
  }

  private fun renderTowers() {
    gameController.towers.onEach {
      renderSpriteWithHealthBar(towerSprite, it)
    }

  }

  private fun renderSpriteWithHealthBar(sprite: Sprite, entity: Tower) {
    val towerSprite = Sprite(sprite)
    val spriteSize = entity.size * 2
    towerSprite.x = entity.body.position.x - entity.size
    towerSprite.y = entity.body.position.y - entity.size
    towerSprite.setSize(spriteSize, spriteSize)
    towerSprite.setOriginCenter()
    towerSprite.draw(batch)
    renderHealthSprite(entity)
  }

  private fun renderFastTowers() {
    gameController.fastTowers.onEach {
      when (it) {
        is TripleShotFastTower -> renderFastTower(fastTowerSprites.get(1), it)
        else -> renderFastTower(fastTowerSprites.get(0), it)
      }
    }
  }

  private fun renderFastTower(fastTowerSprite: Sprite, tower: FastTower) {
    val towerSprite = Sprite(fastTowerSprite)
    val spriteSize = tower.size * 2
    towerSprite.x = tower.body.position.x - tower.size
    towerSprite.y = tower.body.position.y - tower.size
    towerSprite.setSize(spriteSize, spriteSize)
    towerSprite.setOriginCenter()
    towerSprite.rotation = MathUtils.radiansToDegrees * tower.angle

    towerSprite.draw(batch)

    renderHealthSprite(tower)
  }
}
