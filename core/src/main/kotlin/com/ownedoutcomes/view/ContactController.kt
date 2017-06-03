package com.ownedoutcomes.view

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import com.ownedoutcomes.entity.Castle
import com.ownedoutcomes.entity.Enemy

class ContactController(val gameController: GameController) : ContactListener {
  override fun endContact(contact: Contact?) {}

  override fun beginContact(contact: Contact?) {
    checkContact(contact!!.fixtureA.userData, contact.fixtureB.userData)
    checkContact(contact.fixtureB.userData, contact.fixtureA.userData)
  }

  private fun checkContact(firstEntity: Any, secondEntity: Any) {
    if (firstEntity is Enemy && secondEntity is Castle) {
      firstEntity.life--
      if (firstEntity.life < 0) {
        gameController.enemiesToRemove.add(firstEntity)
      }
    }
  }


  override fun preSolve(contact: Contact?, oldManifold: Manifold?) {}
  override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {}
}
