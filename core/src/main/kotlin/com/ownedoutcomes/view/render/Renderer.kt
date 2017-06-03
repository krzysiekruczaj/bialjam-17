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
import com.ownedoutcomes.entity.Chicken
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

  private fun renderBullets() {
    val bullets = gameController.bullets
    bullets.onEach { bullet ->
      val bulletSprite = Sprite(bulletSprite)
      val spriteSize = bullet.size * 2
      bulletSprite.x = bullet.body.position.x - bullet.size
      bulletSprite.y = bullet.body.position.y - bullet.size
      bulletSprite.setSize(spriteSize, spriteSize)
      bulletSprite.setOriginCenter()
      bulletSprite.draw(batch)
    }
  }

  private fun renderCastle() {
    val castle = gameController.castle
    val playerSprite = Sprite(castleSprite)
    val spriteSize = castle.size * 2
    playerSprite.x = castle.body.position.x - castle.size
    playerSprite.y = castle.body.position.y - castle.size
    playerSprite.setSize(spriteSize, spriteSize)
    playerSprite.setOriginCenter()
    playerSprite.draw(batch)

    val pixmap = Pixmap(0, 10, Pixmap.Format.RGBA8888)
    pixmap.setColor(Color.RED)
    pixmap.fill()
    val drawable = TextureRegion(Texture(pixmap))
    batch.color = Color.RED
    batch.draw(drawable, playerSprite.x, playerSprite.y + 2 * castle.size, spriteSize * (castle.life / castle.maxLife), 10f)
    pixmap.dispose()
  }

  private fun renderEnemies(playerPosition: Vector2) {
    println("Rendering ${gameController.enemies.size} enemies")
    gameController.enemies.forEach {
      enemy ->
      renderEnemy(playerPosition, enemy)
    }
  }

  private fun renderEnemy(playerPosition: Vector2, enemy: Chicken) {
    val enemySprite = Sprite(enemySprite)
    val spriteSize = enemy.size * 2
    val enemyBody = enemy.body
    enemySprite.x = enemyBody.position.x - enemy.size
    enemySprite.y = enemyBody.position.y - enemy.size
    enemySprite.setSize(spriteSize, spriteSize)
    enemySprite.setOriginCenter()

    enemySprite.rotation = MathUtils.radiansToDegrees * -enemy.angle
    enemySprite.draw(batch)
  }

  private fun renderTowers() {
    println("Rendering ${gameController.towers.size} towers")
    gameController.towers.forEach {
      tower ->
      renderTower(tower)
    }
  }

  private fun renderTower(tower: Tower) {
    val towerSprite = Sprite(towerSprite)
    val spriteSize = tower.size * 2
    towerSprite.x = tower.body.position.x - tower.size
    towerSprite.y = tower.body.position.y - tower.size
    towerSprite.setSize(spriteSize, spriteSize)
    towerSprite.setOriginCenter()
    towerSprite.draw(batch)

    val pixmap = Pixmap(0, 10, Pixmap.Format.RGBA8888)
    pixmap.setColor(Color.RED)
    pixmap.fill()
    val drawable = TextureRegion(Texture(pixmap))
    batch.color = Color.RED
    batch.draw(drawable, towerSprite.x, towerSprite.y + 2 * tower.size, spriteSize * (tower.life / tower.maxLife), 10f)
    pixmap.dispose()
  }

  private fun renderFastTowers() {
    println("Rendering ${gameController.fastTowers.size} bullets")
    gameController.fastTowers.forEach {
      tower ->
      renderFastTower(tower)
    }
  }

  private fun renderFastTower(tower: FastTower) {
    val towerSprite = Sprite(fastTowerSprite)
    val spriteSize = tower.size * 2
    towerSprite.x = tower.body.position.x - tower.size
    towerSprite.y = tower.body.position.y - tower.size
    towerSprite.setSize(spriteSize, spriteSize)
    towerSprite.setOriginCenter()
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
