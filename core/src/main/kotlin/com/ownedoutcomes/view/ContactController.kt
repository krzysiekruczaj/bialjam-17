package com.ownedoutcomes.view

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold

class ContactController() : ContactListener {
  override fun endContact(contact: Contact?) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun beginContact(contact: Contact?) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}
