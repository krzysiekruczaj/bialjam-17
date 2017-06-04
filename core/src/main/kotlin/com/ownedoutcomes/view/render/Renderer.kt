package com.ownedoutcomes.view.render

import com.badlogic.gdx.graphics.Color
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
import com.ownedoutcomes.view.GameController


class GameRenderer(val gameController: GameController, val batch: Batch, skin: Skin) {
  private val castleSprite = skin.atlas.createSprite("castle_v1")
  private val enemySprite = skin.atlas.createSprite("chicken2_v1")
  private val towerSprite = skin.atlas.createSprite("tower0")
  private val fastTowerSprite = skin.atlas.createSprite("tower1")
  private val bulletSprite = skin.atlas.createSprite("bullet")


  val debugRenderer = Box2DDebugRenderer()


  init {
    castleSprite.setOriginCenter()
    enemySprite.setOriginCenter()
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

    batch.end()
  }

  private fun processSprite(sprite: Sprite, entity: AbstractEntity): Sprite {
    val bulletSprite = Sprite(sprite)
    val spriteSize = entity.size * 2
    bulletSprite.x = entity.body.position.x - entity.size
    bulletSprite.y = entity.body.position.y - entity.size
    bulletSprite.setSize(spriteSize, spriteSize)
    bulletSprite.setOriginCenter()
    bulletSprite.draw(batch)
    return bulletSprite
  }

  private fun renderBullets() {
    gameController.bullets.onEach {
      processSprite(bulletSprite, it)
    }
  }

  private fun renderCastle() {
    val castle = gameController.castle
    val castleSprite = processSprite(castleSprite, castle)

    val pixmap = Pixmap(0, 10, Pixmap.Format.RGBA8888)
    pixmap.setColor(Color.RED)
    pixmap.fill()
    val drawable = TextureRegion(Texture(pixmap))
    batch.color = Color.RED
    batch.draw(drawable, castleSprite.x, castleSprite.y + 2 * castle.size, castle.size * 2 * (castle.life / castle.maxLife), 10f)
    pixmap.dispose()
  }

  private fun renderEnemies(playerPosition: Vector2) {
    gameController.enemies.onEach {
      processSprite(enemySprite, it)
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

    val pixmap = Pixmap(0, 10, Pixmap.Format.RGBA8888)
    pixmap.setColor(Color.RED)
    pixmap.fill()
    val drawable = TextureRegion(Texture(pixmap))
    batch.color = Color.RED
    batch.draw(drawable, towerSprite.x, towerSprite.y + 2 * entity.size, spriteSize * (entity.life / entity.maxLife), 10f)
    pixmap.dispose()
  }

  private fun renderFastTowers() {
//    println("Rendering ${gameController.fastTowers.size} bullets")
    gameController.fastTowers.onEach {
      renderFastTower(fastTowerSprite, it)
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

    val pixmap = Pixmap(0, 10, Pixmap.Format.RGBA8888)
    pixmap.setColor(Color.RED)
    pixmap.fill()
    val drawable = TextureRegion(Texture(pixmap))
    batch.color = Color.RED
    batch.draw(drawable, towerSprite.x, towerSprite.y + 2 * tower.size, spriteSize * (tower.life / tower.maxLife), 10f)
    pixmap.dispose()
  }
}
