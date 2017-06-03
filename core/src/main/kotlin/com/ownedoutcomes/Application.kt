package com.ownedoutcomes

import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.ownedoutcomes.view.ContactController
import com.ownedoutcomes.view.Game
import com.ownedoutcomes.view.Menu
import ktx.app.KtxGame
import ktx.async.enableKtxCoroutines
import ktx.inject.Context
import ktx.math.vec2
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
      val world = World(vec2(0f, 0f), true)
      world.setContactListener(ContactController())
      bindSingleton(world)

      bindSingleton(TextureAtlas("skin.atlas"))
      bindSingleton<Batch>(SpriteBatch())
      bindSingleton<Viewport>(ScreenViewport())
      bindSingleton(Stage(inject(), inject()))
      bindSingleton(createSkin(inject()))
      Scene2DSkin.defaultSkin = inject()
      bindSingleton(this@Application)
      bindSingleton(Menu(inject(), inject()))
      bindSingleton(Game(inject(), inject(), inject()))
    }

    addScreen(context.inject<Menu>())
    addScreen(context.inject<Game>())
    setScreen<Game>()
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

    // Dirt
    imageButton(name = "dirt") {
      up = skin.getDrawable("dirt")
      checked = skin.getDrawable("dirt-selected")
      imageUp = skin.getDrawable("empty-box")
      imageChecked = skin.getDrawable("upgrade-box")
      imageOver = skin.getDrawable("upgrade-box")
    }

    // Tower borders
    val grassArray = arrayOf("n", "ne", "e", "s", "sw", "se", "w", "nw")
    grassArray.forEach { grassStyle ->
      val imageButtonName = "grass_$grassStyle"
      imageButton(name = imageButtonName) {
        up = skin.getDrawable(imageButtonName)
        checked = skin.getDrawable("$imageButtonName-selected")
        imageUp = skin.getDrawable("empty-box")
        imageOver = skin.getDrawable("upgrade-box")
        imageOver = skin.getDrawable("upgrade-box")
      }
    }
  }

  override fun dispose() {
    context.dispose()
  }
}
