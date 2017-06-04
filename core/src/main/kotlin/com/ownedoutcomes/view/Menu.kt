package com.ownedoutcomes.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.ownedoutcomes.Application
import ktx.actors.onClick
import ktx.app.KtxScreen
import ktx.scene2d.label
import ktx.scene2d.table

class Menu(
  val stage: Stage,
  val application: Application) : KtxScreen {
  val view = table {
    setFillParent(true)

    label("Play!") {
      color = Color.WHITE

      fontScaleX = 6f
      fontScaleY = 6f
      onClick { _, _ ->
        application.setScreen<Game>()
      }
    }

    background("background")
  }

  override fun show() {
    stage.addActor(view)
    Gdx.input.inputProcessor = stage
  }

  override fun render(delta: Float) {
    stage.act(delta)
    stage.draw()
  }

  override fun hide() {
    view.remove()
  }
}
