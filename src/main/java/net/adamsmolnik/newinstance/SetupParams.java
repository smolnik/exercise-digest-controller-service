package net.adamsmolnik.newinstance;

/**
 * @author ASmolnik
 *
 */
public class SetupParams implements SetupParamsView {

    private String label;

    private String instanceType;

    private String imageId;

    private String serviceContext;

    public SetupParams withLabel(String value) {
        label = value;
        return this;
    }

    public SetupParams withInstanceType(String value) {
        instanceType = value;
        return this;
    }

    public SetupParams withImageId(String value) {
        imageId = value;
        return this;
    }

    public SetupParams withServiceContext(String value) {
        serviceContext = value;
        return this;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getInstanceType() {
        return instanceType;
    }

    @Override
    public String getImageId() {
        return imageId;
    }

    @Override
    public String getServiceContext() {
        return serviceContext;
    }

}
