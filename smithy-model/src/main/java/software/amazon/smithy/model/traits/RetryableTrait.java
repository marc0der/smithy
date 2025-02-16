/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

package software.amazon.smithy.model.traits;

import java.util.Objects;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.utils.ToSmithyBuilder;

/**
 * Marks an error structure as retryable.
 */
public final class RetryableTrait extends AbstractTrait implements ToSmithyBuilder<RetryableTrait> {
    public static final ShapeId ID = ShapeId.from("smithy.api#retryable");
    private static final String THROTTLING = "throttling";

    private final boolean throttling;

    private RetryableTrait(Builder builder) {
        super(ID, builder.getSourceLocation());
        throttling = builder.throttling;
    }

    /**
     * Creates a builder for a retryable trait.
     *
     * @return Returns the created builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * @return Returns true if the retry is a throttle.
     */
    public boolean getThrottling() {
        return throttling;
    }

    @Override
    public Builder toBuilder() {
        return builder().sourceLocation(getSourceLocation()).throttling(throttling);
    }

    @Override
    protected Node createNode() {
        ObjectNode.Builder nodeBuilder = Node.objectNodeBuilder().sourceLocation(getSourceLocation());
        if (throttling) {
            nodeBuilder.withMember(THROTTLING, true);
        }
        return nodeBuilder.build();
    }

    // Avoid equality issues if, for example, throttling is set explicitly to false.
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof RetryableTrait)) {
            return false;
        } else if (other == this) {
            return true;
        } else {
            RetryableTrait ot = (RetryableTrait) other;
            return throttling == ot.throttling;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(toShapeId(), throttling);
    }

    public static final class Provider implements TraitService {
        @Override
        public ShapeId getShapeId() {
            return ID;
        }

        @Override
        public RetryableTrait createTrait(ShapeId target, Node value) {
            ObjectNode node = value.expectObjectNode();
            Builder builder = builder().sourceLocation(value.getSourceLocation());
            RetryableTrait result = builder.throttling(node.getBooleanMemberOrDefault(THROTTLING)).build();
            result.setNodeCache(value);
            return result;
        }
    }

    /**
     * Builds a {@link RetryableTrait} trait.
     */
    public static final class Builder extends AbstractTraitBuilder<RetryableTrait, Builder> {
        private boolean throttling;

        private Builder() {}

        @Override
        public RetryableTrait build() {
            return new RetryableTrait(this);
        }

        /**
         * Indicates if the retry is considered a throttle.
         *
         * @param throttling Set to true if the retry is a throttle.
         * @return Returns the builder.
         */
        public Builder throttling(boolean throttling) {
            this.throttling = throttling;
            return this;
        }
    }
}
