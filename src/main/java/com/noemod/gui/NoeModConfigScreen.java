package com.noemod.gui;

import com.noemod.config.AutoFarmConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class NoeModConfigScreen extends Screen {

	private final Screen parent;

	public NoeModConfigScreen(Screen parent) {
		super(Text.translatable("text.noemod.config.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int centerX = this.width / 2;
		int startY = 32;
		int widgetWidth = 240;
		int widgetHeight = 20;
		int spacing = 24;

		// 1. Slider Radius (1.0 s/d 6.0)
		this.addDrawableChild(new SliderWidget(
			centerX - widgetWidth / 2, startY, widgetWidth, widgetHeight,
			Text.translatable("text.noemod.config.radius", String.format("%.1f", AutoFarmConfig.radius)),
			(AutoFarmConfig.radius - 1.0) / 5.0
		) {
			@Override
			protected void updateMessage() {
				this.setMessage(Text.translatable("text.noemod.config.radius", String.format("%.1f", AutoFarmConfig.radius)));
			}

			@Override
			protected void applyValue() {
				AutoFarmConfig.radius = Math.round((1.0 + this.value * 5.0) * 10.0) / 10.0;
			}
		});

		// 2. Slider Tick Delay (1 s/d 20 tick)
		this.addDrawableChild(new SliderWidget(
			centerX - widgetWidth / 2, startY + spacing, widgetWidth, widgetHeight,
			Text.translatable("text.noemod.config.tick_delay", AutoFarmConfig.tickDelay),
			(AutoFarmConfig.tickDelay - 1) / 19.0
		) {
			@Override
			protected void updateMessage() {
				this.setMessage(Text.translatable("text.noemod.config.tick_delay", AutoFarmConfig.tickDelay));
			}

			@Override
			protected void applyValue() {
				AutoFarmConfig.tickDelay = (int) Math.round(1 + this.value * 19);
			}
		});

		// 3. Toggle Gunakan Cangkul (Use Hoe)
		this.addDrawableChild(CyclingButtonWidget.onOffBuilder(AutoFarmConfig.useHoe)
			.build(
				centerX - widgetWidth / 2, startY + spacing * 2, widgetWidth, widgetHeight,
				Text.translatable("text.noemod.config.use_hoe"),
				(button, value) -> AutoFarmConfig.useHoe = value
			));

		// 4. Toggle Tanam Otomatis (Auto Replant)
		this.addDrawableChild(CyclingButtonWidget.onOffBuilder(AutoFarmConfig.autoReplant)
			.build(
				centerX - widgetWidth / 2, startY + spacing * 3, widgetWidth, widgetHeight,
				Text.translatable("text.noemod.config.auto_replant"),
				(button, value) -> AutoFarmConfig.autoReplant = value
			));

		// 5. Toggle Proteksi Farmland Trample
		this.addDrawableChild(CyclingButtonWidget.onOffBuilder(AutoFarmConfig.protectFarmland)
			.build(
				centerX - widgetWidth / 2, startY + spacing * 4, widgetWidth, widgetHeight,
				Text.translatable("text.noemod.config.protect_farmland"),
				(button, value) -> AutoFarmConfig.protectFarmland = value
			));

		// 6. Toggle Memori Lahan (Farm Memory)
		this.addDrawableChild(CyclingButtonWidget.onOffBuilder(AutoFarmConfig.enableFarmMemory)
			.build(
				centerX - widgetWidth / 2, startY + spacing * 5, widgetWidth, widgetHeight,
				Text.translatable("text.noemod.config.enable_farm_memory"),
				(button, value) -> AutoFarmConfig.enableFarmMemory = value
			));

		// 7. Tombol Selesai & Simpan (Done)
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE, button -> {
			AutoFarmConfig.save();
			if (this.client != null) {
				this.client.setScreen(this.parent);
			}
		}).dimensions(centerX - 100, startY + spacing * 6 + 6, 200, 20).build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 12, 0xFFFFFF);
	}

	@Override
	public void close() {
		AutoFarmConfig.save();
		if (this.client != null) {
			this.client.setScreen(this.parent);
		}
	}
}
