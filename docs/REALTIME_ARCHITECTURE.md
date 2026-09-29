# Real-Time Architecture

## Protocol
WebSocket over TLS using STOMP semantics.

## Connection
Client authenticates with short-lived access token.
Server validates tenant and user before allowing subscriptions.

## Topics

User:
`/user/queue/notifications`

Tenant:
`/topic/tenant/{tenantId}/announcements`

Class:
`/topic/class/{classId}/timetable`

Exam:
`/topic/exam/{examId}/status`

## Event Envelope

```json
{
  "eventId": "uuid",
  "type": "RESULT_PUBLISHED",
  "version": 1,
  "occurredAt": "2026-09-29T15:30:00Z",
  "tenantId": "uuid",
  "aggregateId": "uuid",
  "payload": {}
}
```

## Reliability

WebSocket delivery is not the source of truth.

On:
- reconnect;
- missed event;
- token refresh;
- app resume;

client calls REST synchronization endpoint.

## Outbox Pattern

1. Commit business transaction.
2. Insert outbox event in same transaction.
3. Worker reads unpublished events.
4. Publish to WebSocket/push adapters.
5. Mark published.
6. Retry failures with backoff.

## Ordering

Events for the same aggregate should carry monotonically increasing sequence/version where required.

## Duplicate Handling

Clients store recently handled event IDs or use entity versions.

## Push

WebSocket is preferred while online.
FCM/APNs notification is used when mobile is backgrounded.

## Scaling

For multiple API instances:
- Redis pub/sub or broker-backed event fanout.
- Shared session/token strategy.
- Sticky sessions are not a correctness requirement.

## Security

Never broadcast:
- another student's marks;
- private documents;
- internal audit details

to unauthorized topics.
