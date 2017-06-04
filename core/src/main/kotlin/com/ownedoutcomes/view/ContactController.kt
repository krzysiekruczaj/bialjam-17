package com.ownedoutcomes.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.ownedoutcomes.entity.*
import ktx.assets.toInternalFile

class ContactController(val gameController: GameController, val assetManager: AssetManager) : ContactListener {
  override fun endContact(contact: Contact?) {}

  override fun beginContact(contact: Contact?) {
    if (contact != null) {
      checkContact(contact.fixtureA.userData, contact.fixtureB.userData)
      checkContact(contact.fixtureB.userData, contact.fixtureA.userData)
    }
  }

  private fun checkContact(firstEntity: Any, secondEntity: Any) {
    if (firstEntity is Chicken) {
      when (secondEntity) {
        is Castle -> {
          secondEntity.life--
          if (secondEntity.life < 0) {
            gameController.gameOver()
          }
        }
        is Bullet -> {
          decreaseLifeForEnemyAndAssignForRemovalIfNeeded(firstEntity, secondEntity.power)
          gameController.bulletsToRemove.add(secondEntity)
          gameController.points += firstEntity.level * 2 + 1
          Gdx.audio.newSound("hit${MathUtils.random.nextInt(10)}.wav".toInternalFile()).play(0.4f)
        }
        is Tower -> {
          secondEntity.life--
          if (secondEntity.life < 0) {
            gameController.towersToRemove.add(secondEntity)
          }
        }
        is FastTower -> {
          secondEntity.life -= firstEntity.level * 2 + 1
          if (secondEntity.life < 0) {
            gameController.fastTowersToRemove.add(secondEntity)
          }
        }
      }
    }

  }

  private fun decreaseLifeForEnemyAndAssignForRemovalIfNeeded(firstEntity: Chicken, damage: Float) {
    firstEntity.life -= damage
    if (firstEntity.life < 0) {
      gameController.enemiesToRemove.add(firstEntity)
      Gdx.audio.newSound("hit0.wav".toInternalFile()).play(0.4f)

    }
  }

  override fun preSolve(contact: Contact?, oldManifold: Manifold?) {}
  override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {}
}

// Collision groups:
val enemyGroup: Short = 0b00000001
val bulletGroup: Short = 0b00000010
val towerGroup: Short = 0b00000100

val enemyCollisionGroup: Short = 0b00000110
val bulletCollisionGroup: Short = 0b00000001
val towerCollisionGroup: Short = 0b00000001
