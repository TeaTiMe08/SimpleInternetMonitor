package de.teatime08.config;

import de.teatime08.util.ResourceUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class AvailibleProviders extends HashSet<AvailibleProviders.Provider> {
    public AvailibleProviders() throws IOException {
        String fileContent = new String(ResourceUtil.loadResource("/pingDomains.csv", AvailibleProviders.class));
        String lines[] = fileContent.replace("\r\n", "\n").split("\n");
        final List<Provider> providers = Arrays.stream(lines)
            .skip(1) // skip headers
            .map(line -> line.split(","))
            .map(csvArr -> new Provider(
                csvArr[ProviderCSVModel.COUNTRY.ordinal()],
                csvArr[ProviderCSVModel.PROVIDER_NAME.ordinal()],
                csvArr[ProviderCSVModel.DOMAIN_OR_IP.ordinal()]
            ))
            .collect(Collectors.toList());
        addAll(providers);
    }

    public enum ProviderCSVModel {
        COUNTRY,
        PROVIDER_NAME,
        DOMAIN_OR_IP;
    }
    public class Provider {
        public final String country, providerName, domainOrIp;
        public Provider(String country, String providerName, String domainOrIp) {
            this.country = country;
            this.providerName = providerName;
            this.domainOrIp = domainOrIp;
        }
    }
}
