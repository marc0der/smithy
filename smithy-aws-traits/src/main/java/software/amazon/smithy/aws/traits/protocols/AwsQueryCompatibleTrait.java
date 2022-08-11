/*
 * Copyright 2022 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.smithy.aws.traits.protocols;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import software.amazon.smithy.model.SourceException;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.node.StringNode;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.model.traits.AbstractTraitBuilder;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.utils.ToSmithyBuilder;

public final class AwsQueryCompatibleTrait extends AbstractTrait
        implements ToSmithyBuilder<AwsQueryCompatibleTrait> {

    public static final ShapeId ID = ShapeId.from("aws.protocols#awsQueryCompatible");

    private List<AwsQueryCompatibleError> errors;

    public AwsQueryCompatibleTrait(Builder builder) {
        super(ID, builder.getSourceLocation());
        this.errors = builder.errors;
    }

    @Override
    protected Node createNode() {
        return errors.stream().collect(
            ObjectNode.collect(
                error -> Node.from(error.getException()),
                error -> Node.from(toObjectNode(error))
            )
        );
    }

    public List<AwsQueryCompatibleError> getErrors() {
        return errors;
    }

    private static ObjectNode toObjectNode(AwsQueryCompatibleError error) {

        ObjectNode.Builder builder = ObjectNode.builder();

        if (error.getCode() != null || !error.getCode().isEmpty()) {
            builder.withMember("code", Node.from(error.getCode()));
        }

        if (error.getHttpResponseCode() != null) {
            builder.withMember("httpResponseCode", Node.from(error.getHttpResponseCode()));
        }

        return builder.build();
    }

    @Override
    public AwsQueryCompatibleTrait.Builder toBuilder() {
        AwsQueryCompatibleTrait.Builder builder = new Builder().sourceLocation(getSourceLocation());
        errors.forEach(builder::addError);
        return builder;
    }

    public static AwsQueryCompatibleTrait.Builder builder() {
        return new AwsQueryCompatibleTrait.Builder();
    }


    /**
     * Builder used to create the external documentation trait.
     */
    public static final class Builder extends AbstractTraitBuilder<AwsQueryCompatibleTrait, Builder> {
        private final List<AwsQueryCompatibleError> errors = new ArrayList<>();

        public AwsQueryCompatibleTrait.Builder addError(AwsQueryCompatibleError error) {
            errors.add(Objects.requireNonNull(error));
            return this;
        }

        @Override
        public AwsQueryCompatibleTrait build() {
            return new AwsQueryCompatibleTrait(this);
        }
    }

    public static final class Provider extends AbstractTrait.Provider {
        public Provider() {
            super(ID);
        }

        @Override
        public Trait createTrait(ShapeId id, Node value) {
            AwsQueryCompatibleTrait.Builder builder = builder().sourceLocation(value);
            value.expectObjectNode().getMembers().forEach((k, v) -> {

                String exception = k.expectStringNode().getValue();
                String code = null;
                Integer httpResponseCode = null;

                for (Map.Entry<StringNode, Node> entry: v.expectObjectNode().getMembers().entrySet()) {
                    String member = entry.getKey().getValue();
                    switch (member) {
                        case "code":
                            code = entry.getValue().expectStringNode().getValue();
                            break;
                        case "httpResponseCode":
                            httpResponseCode = entry.getValue().expectNumberNode().getValue().intValue();
                            break;
                        default:
                            throw new SourceException(String.format(
                                    "Unsupported @awsQueryCompatibleTrait value member '%s'", member), value);
                    }
                }
                builder.addError(new AwsQueryCompatibleError(exception, code, httpResponseCode));
            });

            return builder.build();
        }
    }
}
