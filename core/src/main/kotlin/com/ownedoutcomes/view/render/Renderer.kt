package com.ownedoutcomes.logic

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.ownedoutcomes.entity.Chicken
import com.ownedoutcomes.entity.Tower
import com.ownedoutcomes.view.GameController


class GameRenderer(val gameController: GameController, val batch: Batch, skin: Skin) {
  private val castleSprite = skin.atlas.createSprite("castle_grey")
  private val enemySprite = skin.atlas.createSprite("chicken2_v1")
  private val towerSprite = skin.atlas.createSprite("tower0")

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
    renderEnemies()
    renderTowers()

    batch.end()
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
  }

  private fun renderEnemies() {
    println("Rendering ${gameController.enemies.size} enemies")
    gameController.enemies.forEach {
      enemy ->
      renderEnemy(enemy)
    }
  }

  private fun renderEnemy(enemy: Chicken) {
    val enemySprite = Sprite(enemySprite)
    val spriteSize = enemy.size * 2
    enemySprite.x = enemy.body.position.x - enemy.size
    enemySprite.y = enemy.body.position.y - enemy.size
    enemySprite.setSize(spriteSize, spriteSize)
    enemySprite.setOriginCenter()
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
    val enemySprite = Sprite(towerSprite)
    val spriteSize = tower.size * 2
    enemySprite.x = tower.body.position.x - tower.size
    enemySprite.y = tower.body.position.y - tower.size
    enemySprite.setSize(spriteSize, spriteSize)
    enemySprite.setOriginCenter()
    enemySprite.draw(batch)
  }
}
