package com.juanelsuper.minecoin.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ATMScreen extends ContainerScreen<ATMContainer> {
	
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("minecoin:textures/gui/containers/atm.png");
	private static final TranslationTextComponent WITHDRAW_TEXT = new TranslationTextComponent("gui.minecoin.withdraw");
	private static final TranslationTextComponent DEPOSIT_TEXT = new TranslationTextComponent("gui.minecoin.deposit");
	private TextFieldWidget tf_widthraw;
	private int widthraw_amount;

	public ATMScreen(ATMContainer containerIn, PlayerInventory playerInv, ITextComponent title) {
		super(containerIn, playerInv, title);
	}
	
	public void tick() {
      super.tick();
      this.tf_widthraw.tick();
   }
	
	protected void init() {
		super.init();
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		int i = this.leftPos;
		int j = this.topPos;
		this.tf_widthraw = new TextFieldWidget(this.font, i+27, j+43, 50, 12, title);
        this.tf_widthraw.setTextColor(0x82fe2f);
        this.tf_widthraw.setTextColorUneditable(0x82fe2f);
		this.tf_widthraw.setEditable(true);
		this.tf_widthraw.setVisible(true);
		this.tf_widthraw.setMaxLength(5);
		this.tf_widthraw.setValue("$0");
		this.tf_widthraw.setResponder(this::onValueChanged);
		this.children.add(tf_widthraw);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
		super.render(matrixStack, mouseX, mouseY, partialTick);
		this.renderFg(matrixStack, mouseX, mouseY, partialTick);
	}
	
	@Override
	protected void renderBg(MatrixStack matrixStack, float partialTick, int mouseX, int mouseY) {
		this.renderBackground(matrixStack);
		this.minecraft.getTextureManager().bind(BACKGROUND_TEXTURE);
		int i = this.leftPos;
		int j = this.topPos;
		this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
		this.addButton(new ImageButton(i + 25, j + 62, 54, 17, 0, 166, 17, BACKGROUND_TEXTURE, (button)->{
			this.menu.withdraw(widthraw_amount);
		}));
		this.addButton(new ImageButton(i + 97, j + 62, 54, 17, 0, 200, 17, BACKGROUND_TEXTURE, (button)->{
			this.menu.deposit();
		}));

	}
	
	public void renderFg(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
		this.tf_widthraw.render(matrixStack, mouseX, mouseY, partialTick);
    }
	
	public void removed() {
	      super.removed();
	      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
	   }
	
	private int centerTextX(ITextComponent text, int middle) {
		int k = middle - (this.font.width(text)/2);
		return k + 2;
	}
	
	@Override
	protected void renderLabels(MatrixStack stack, int mouseX, int mouseY) {
		int balance = this.menu.getBalance(this.inventory.player);
		ITextComponent balanceComponent;
		balanceComponent = new TranslationTextComponent("$" + balance);
		this.font.drawShadow(stack, balanceComponent, 25F + centerTextX(balanceComponent, 25), 11F, 0x82fe2f);
		this.font.drawShadow(stack, WITHDRAW_TEXT, 25F + centerTextX(WITHDRAW_TEXT, 26), 62+4, -1);
		this.font.drawShadow(stack, DEPOSIT_TEXT, 97F + centerTextX(DEPOSIT_TEXT, 26), 62+4, -1);
		
	}
	private void onValueChanged(String text) {
		if(text.length() >= 2 && text.charAt(0) == '$') {
			text = text.substring(1);
			try {
				widthraw_amount = Integer.parseInt(text);
			}catch(NumberFormatException e) {}
		}
		else {
			widthraw_amount = 0;
		}
		if(widthraw_amount > 3200) {
			widthraw_amount = 3200;
		}
		text = "$" + (widthraw_amount == 0 ? "": widthraw_amount);
		if(!this.tf_widthraw.getValue().equals(text)) {
			this.tf_widthraw.setValue(text);
		}
		
	}
	

}
