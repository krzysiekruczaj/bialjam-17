package com.ownedoutcomes.view

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.ownedoutcomes.Application
import com.ownedoutcomes.entity.*
import ktx.inject.Context

class ContactController(val context: Context, val gameController: GameController) : ContactListener {
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
          decreaseLifeForEnemyAndAssignForRemovalIfNeeded(firstEntity)
          secondEntity.life--
          if (secondEntity.life < 0) {
            context.inject<Application>().setScreen<Menu>()
          }
        }
        is Bullet -> {
          decreaseLifeForEnemyAndAssignForRemovalIfNeeded(firstEntity)
          gameController.bulletsToRemove.add(secondEntity)
        }
        is Tower -> {
          decreaseLifeForEnemyAndAssignForRemovalIfNeeded(firstEntity)

          secondEntity.life--
          if (secondEntity.life < 0) {
            gameController.towersToRemove.add(secondEntity)
          }
        }
        is FastTower -> {
          decreaseLifeForEnemyAndAssignForRemovalIfNeeded(firstEntity)

          secondEntity.life--
          if (secondEntity.life < 0) {
            gameController.fastTowersToRemove.add(secondEntity)
          }
        }

      }
    }

  }

  private fun decreaseLifeForEnemyAndAssignForRemovalIfNeeded(firstEntity: Chicken) {
    firstEntity.life--
    if (firstEntity.life < 0) {
      gameController.enemiesToRemove.add(firstEntity)
    }
  }

  override fun preSolve(contact: Contact?, oldManifold: Manifold?) {}
  override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {}
}
