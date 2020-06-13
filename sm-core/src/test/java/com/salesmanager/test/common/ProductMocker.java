package com.salesmanager.test.common;

import com.salesmanager.core.model.catalog.product.Product;
import com.salesmanager.core.model.merchant.MerchantStore;
import org.junit.Ignore;

import static org.mockito.Mockito.mock;

/**
 * Helps mocking products.
 * @author <a href="mailto:luca@camphuisen.com">Luca Camphuisen</a>
 * @since 6/13/20
 */
@Ignore
public final class ProductMocker {

    public static final boolean DEFAULT_VIRTUAL = true;
    public static Product mockProduct(long id, MerchantStore merchantStore) {
        Product mocked = mockProduct(id, merchantStore, DEFAULT_VIRTUAL);
        return mocked;
    }

    public static Product mockProduct(long id, MerchantStore merchantStore, boolean virtual) {
        Product mocked = mock(Product.class);
        mocked.setId(id);
        mocked.setMerchantStore(merchantStore);
        mocked.setProductVirtual(virtual);
        return mocked;
    }
}
