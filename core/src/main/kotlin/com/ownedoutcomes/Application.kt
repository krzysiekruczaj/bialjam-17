package com.ownedoutcomes

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.FitViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.ownedoutcomes.view.Game
import com.ownedoutcomes.view.Menu
import ktx.app.KtxGame
import ktx.async.enableKtxCoroutines
import ktx.inject.Context
import ktx.scene2d.Scene2DSkin
import ktx.style.defaultStyle
import ktx.style.imageButton
import ktx.style.label
import ktx.style.skin

class Application : KtxGame<Screen>() {
  val context = Context()

  override fun create() {
    enableKtxCoroutines(asynchronousExecutorConcurrencyLevel = 1)
    context.register {
      bindSingleton(TextureAtlas("skin.atlas"))
      bindSingleton<Batch>(SpriteBatch())
      bindSingleton<Viewport>(ScreenViewport())
      bindSingleton(Stage(inject(), inject()))
      bindSingleton(createSkin(inject()))
      Scene2DSkin.defaultSkin = inject()
      bindSingleton(this@Application)
      bindSingleton(Menu(inject(), inject()))
      bindSingleton(Game(inject()))
    }

    addScreen(context.inject<Menu>())
    addScreen(context.inject<Game>())
    setScreen<Menu>()
  }

  fun createSkin(atlas: TextureAtlas): Skin = skin(atlas) { skin ->
    add(defaultStyle, BitmapFont())
    label {
      font = skin.getFont(defaultStyle)
    }

    repeat(5) { index ->
      imageButton(name = "tower$index") {
        imageUp = skin.getDrawable("tower$index")
        up = skin.getDrawable("beige")
        checked = skin.getDrawable("gray")
      }
    }

    // Grass
    imageButton(name = "grass") {
      imageUp = skin.getDrawable("grass")
      checked = skin.getDrawable("grass")
    }

    // Dirt
    imageButton(name = "dirt") {
      imageUp = skin.getDrawable("dirt")
      checked = skin.getDrawable("dirt")
    }

    // Tower borders
    val name = "grass_n"
    imageButton(name = name) {
      imageUp = skin.getDrawable(name)
      imageChecked = skin.getDrawable("$name-selected")
    }

    val grassArray = arrayOf("ne", "e", "se", "s", "sw", "w", "nw")
    for (grassStyle in grassArray) {
      val imageButtonName = "grass_$grassStyle"
      imageButton(name = imageButtonName) {
        imageUp = skin.getDrawable(imageButtonName)
        checked = skin.getDrawable(imageButtonName)
      }
    }
  }

  override fun dispose() {
    context.dispose()
  }
}
