/*
 * Copyright 2017, OpenRemote Inc.
 *
 * See the CONTRIBUTORS.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.model.attribute;

import org.openremote.model.event.shared.SharedEventWithAssetId;
import org.openremote.model.value.Value;

import java.util.Objects;
import java.util.Optional;

/**
 * A timestamped {@link AttributeState}.
 */
public class AttributeEvent extends SharedEventWithAssetId {

    public static final String HEADER_SOURCE = AttributeEvent.class.getName() + ".SOURCE";

    /**
     * Processing of the attribute event depends on the origin of the event.
     */
    public enum Source {

        /**
         * The event was created by a client, it's a write request by a user.
         */
        CLIENT,

        /**
         * The event was created by internal processing, for example, as a rule
         * consequence. Protocols can also create events for internal processing,
         * to update any assets' state.
         */
        INTERNAL, // This needs to be more fine grained

        /**
         * An attribute event has been created by the linking service, which enables
         * writing the same value onto another, linked attribute.
         */
        ATTRIBUTE_LINKING_SERVICE,

        /**
         * The event is a value change on a sensor, created by a protocol.
         */
        SENSOR
    }

    protected AttributeState attributeState;

    protected AttributeEvent() {
    }

    public AttributeEvent(String entityId, String attributeName, Value value) {
        this(new AttributeState(new AttributeRef(entityId, attributeName), value));
    }

    public AttributeEvent(String entityId, String attributeName) {
        this(new AttributeState(new AttributeRef(entityId, attributeName)));
    }

    public AttributeEvent(String entityId, String attributeName, boolean deleted) {
        this(new AttributeState(new AttributeRef(entityId, attributeName)));
        attributeState.deleted = deleted;
    }

    public AttributeEvent(String entityId, String attributeName, Value value, long timestamp) {
        this(new AttributeState(new AttributeRef(entityId, attributeName), value), timestamp);
    }

    public AttributeEvent(AttributeRef attributeRef, Value value) {
        this(new AttributeState(attributeRef, value));
    }

    public AttributeEvent(AttributeRef attributeRef) {
        this(new AttributeState(attributeRef));
    }

    public AttributeEvent(AttributeRef attributeRef, Value value, long timestamp) {
        this(new AttributeState(attributeRef, value), timestamp);
    }

    public AttributeEvent(AttributeState attributeState) {
        this.attributeState = attributeState;
    }

    public AttributeEvent(AttributeState attributeState, long timestamp) {
        super(timestamp);
        Objects.requireNonNull(attributeState);
        this.attributeState = attributeState;
    }

    public AttributeState getAttributeState() {
        return attributeState;
    }

    public AttributeRef getAttributeRef() {
        return getAttributeState().getAttributeRef();
    }

    public String getEntityId() {
        return getAttributeRef().getEntityId();
    }

    public String getAttributeName() {
        return getAttributeRef().getAttributeName();
    }

    public Optional<Value> getValue() {
        return getAttributeState().getValue();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
            "timestamp=" + timestamp +
            ", attributeState=" + attributeState +
            "}";
    }
}
