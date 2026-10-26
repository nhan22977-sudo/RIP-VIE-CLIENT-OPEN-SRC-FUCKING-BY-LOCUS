package de.labystudio.spotifyapi.open.totp.provider;

import de.labystudio.spotifyapi.open.totp.model.Secret;
import java.io.IOException;

public interface SecretProvider {
   Secret getSecret() throws IOException;
}
