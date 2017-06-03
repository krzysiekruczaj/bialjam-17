package com.ownedoutcomes.logic

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.ownedoutcomes.entity.Chicken
import com.ownedoutcomes.view.GameController


class GameRenderer(val gameController: GameController, val batch: Batch, skin: Skin) {
  private val towerSprite = skin.atlas.createSprite("castle_grey")
  private val enemySprite = skin.atlas.createSprite("chicken2_v1")


  init {
    towerSprite.setOriginCenter()
    enemySprite.setOriginCenter()
  }

  fun render(delta: Float) {
    batch.projectionMatrix = gameController.camera.combined
    batch.begin()

    renderPlayer()

    renderEnemies()

    batch.end()
  }

  private fun renderPlayer() {
    val castle = gameController.castle
    val playerSprite = Sprite(towerSprite)
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
}
