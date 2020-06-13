package com.salesmanager.test.common;

import com.salesmanager.core.model.merchant.MerchantStore;
import org.junit.Ignore;

import static org.mockito.Mockito.mock;

/**
 * @author <a href="mailto:luca@camphuisen.com">Luca Camphuisen</a>
 * @since 6/13/20
 */
@Ignore
public final class MerchantStoreMocker {

    public static final String DEFAULT_NAME = "MockitoMerch";

    public static MerchantStore mockStore(int id) {
        MerchantStore merchantStore = mockStore(id, DEFAULT_NAME);
        return merchantStore;
    }

    public static MerchantStore mockStore(int id, String name) {
        MerchantStore merchantStore = mock(MerchantStore.class);
        merchantStore.setId(id);
        merchantStore.setStorename(name);
        return merchantStore;
    }
}
