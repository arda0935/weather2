package weather2;

import com.lovetropics.minigames.common.core.game.weather.WeatherState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public final class UpdateWeatherPacket {
	private final WeatherState weather;

	public UpdateWeatherPacket(WeatherState weather) {
		this.weather = weather;
	}

	public void encode(FriendlyByteBuf buffer) {
		this.weather.serialize(buffer);
	}

	public static UpdateWeatherPacket decode(FriendlyByteBuf buffer) {
		WeatherState weather = new WeatherState();
		weather.deserialize(buffer);
		return new UpdateWeatherPacket(weather);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientTickHandler.checkClientWeather();
			ClientWeather.get().onUpdateWeather(this.weather);
		});

		ctx.get().setPacketHandled(true);
	}
}
