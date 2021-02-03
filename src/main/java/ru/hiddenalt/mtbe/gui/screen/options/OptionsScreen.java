package ru.hiddenalt.mtbe.gui.screen.options;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloCall.Callback;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import ru.hiddenalt.mtbe.GetHostInfoQuery;
import ru.hiddenalt.mtbe.GetHostInfoQuery.Data;
import ru.hiddenalt.mtbe.GetHostInfoQuery.HostInfo;
import ru.hiddenalt.mtbe.graphql.ConnectionCheckCallback;
import ru.hiddenalt.mtbe.graphql.GraphqlClient;
import ru.hiddenalt.mtbe.gui.screen.ErrorScreen;

public class OptionsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget hostURLField;
    private String hostName = "";
    private String hostVersion = "";
    private String hostImage = "";
    private boolean firstTimeInited = true;
    private String changedHostName = "";
    private Identifier mResourceLocation = null;

    public OptionsScreen(Screen parent) {
        super(new TranslatableText("menu.title"));
        this.parent = parent;
    }

    protected void init() {
        this.hostURLField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 46, 200, 20, new TranslatableText("addServer.enterName"));
        this.hostURLField.setSelected(false);
        this.hostURLField.setText(GraphqlClient.getHostURL());
        this.hostURLField.setChangedListener(this::onAddressFieldChange);
        this.hostURLField.setMaxLength(99999);
        this.children.add(this.hostURLField);
        int buttonOffset = 40;
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - buttonOffset, 200, 20, ScreenTexts.DONE, (buttonWidget) -> {
            assert this.client != null;

            String hostURL = GraphqlClient.getHostURL();

            try {
                if (!this.changedHostName.trim().equals("") && this.changedHostName != null) {
                    GraphqlClient.setHostURL(this.changedHostName);
                }
            } catch (NullPointerException var4) {
                GraphqlClient.setHostURL(hostURL);
                this.changedHostName = "";
                this.client.openScreen(new ErrorScreen(this, (new TranslatableText("id.error.invalid-host", new Object[]{this.changedHostName, var4.getLocalizedMessage()})).getString()));
                return;
            }

            this.client.openScreen(this.parent);
        }));
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height - buttonOffset - 5 - 20, 200, 20, new TranslatableText("menu.colors.table"), (buttonWidget) -> {
            assert this.client != null;

            this.client.openScreen(new ColorDefinitionTableScreen(this));
        }));
        if (this.firstTimeInited) {
            GraphqlClient.onlineTest(new ConnectionCheckCallback() {
                public void success() {
                    OptionsScreen.this.getHostInfo();
                }

                public void failure() {
                }
            });
        }

        this.firstTimeInited = false;
    }

    private void onAddressFieldChange(String s) {
        if (!s.trim().equals("")) {
            this.changedHostName = s;
        }
    }

    protected void getHostInfo() {
        ApolloClient client = GraphqlClient.getApolloClient();
        client.query(new GetHostInfoQuery()).enqueue(new Callback<Data>() {
            public void onResponse(@NotNull Response<Data> response) {
                HostInfo data = ((Data)Objects.requireNonNull((Data)response.getData())).hostInfo();
                OptionsScreen.this.hostName = data.name();
                OptionsScreen.this.hostVersion = data.version();
                OptionsScreen.this.hostImage = data.image();
                OptionsScreen.this.createImage();
            }

            public void onFailure(@NotNull ApolloException e) {
                OptionsScreen.this.hostName = "-";
                OptionsScreen.this.hostVersion = "-";
                OptionsScreen.this.hostImage = "";
            }
        });
    }

    public void createImage() {
        URL url = null;

        try {
            url = new URL(this.hostImage);
            NativeImage test = NativeImage.read(url.openStream());
            this.mResourceLocation = this.client.getTextureManager().registerDynamicTexture("tmp", new NativeImageBackedTexture(test));
        } catch (IOException var3) {
            this.mResourceLocation = new Identifier("mtbe:textures/menu/unknown.png");
        }

    }

    public void tick() {
        this.hostURLField.tick();
    }

    public void resize(MinecraftClient client, int width, int height) {
        String serverNameFieldText = this.hostURLField.getText();
        this.init(client, width, height);
        this.hostURLField.setText(serverNameFieldText);
    }

    public void removed() {
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
        int infoOffset = 55;
        String statusText = "?";
        int statusColor = 16777215;
        if (GraphqlClient.isIsPendingOnlineStatus()) {
            statusText = I18n.translate("menu.status.pending", new Object[0]);
            statusColor = 15587648;
        } else if (!GraphqlClient.isIsOnline()) {
            statusText = I18n.translate("menu.status.offline", new Object[0]);
            statusColor = 15552064;
        } else {
            statusText = I18n.translate("menu.status.online", new Object[0]);
            statusColor = 11660093;
            drawCenteredText(matrices, this.textRenderer, Text.of(this.hostName), this.width / 2, infoOffset + 10 + 5, 3448555);
            drawCenteredText(matrices, this.textRenderer, Text.of(this.hostVersion), this.width / 2, infoOffset + 20 + 5, 3448555);

            assert this.client != null;

            if (this.mResourceLocation != null) {
                this.client.getTextureManager().bindTexture(this.mResourceLocation);
                int size = (int)Math.round((double)this.height / 3.9D);
                drawTexture(matrices, this.width / 2 - size / 2, this.height / 2 - size / 2, size, size, 0.0F, 0.0F, 16, 128, 16, 128);
            }
        }

        drawCenteredText(matrices, this.textRenderer, Text.of(statusText), this.width / 2 + 100 - this.textRenderer.getWidth(statusText) / 2, 33, statusColor);
        drawStringWithShadow(matrices, this.textRenderer, I18n.translate("menu.hostUrl.tooltip", new Object[0]), this.width / 2 - 100, 33, 10526880);
        this.hostURLField.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
