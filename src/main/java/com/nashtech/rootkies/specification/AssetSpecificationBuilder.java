package com.nashtech.rootkies.specification;

import com.nashtech.rootkies.model.Asset;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AssetSpecificationBuilder {

    private final List<SearchCriteria> params;


    public AssetSpecificationBuilder() {
        params = new ArrayList<SearchCriteria>();
    }

    public AssetSpecificationBuilder with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public Specification<Asset> build() {
        if(params.size() == 0) {
            return null;
        }

        List<Specification> specs = params.stream()
                .map(AssetSpecification::new)
                .collect(Collectors.toList());

        Specification result = specs.get(0);

        for(int i = 1; i < params.size(); i++) {
            result = Specification.where(result)
                    .and(specs.get(i));
        }
        return result;
    }
}
