# Production Overlay Gate

A production overlay is intentionally not generated yet. The release owner must approve:

- Kubernetes provider, cluster topology and namespace policy.
- Container registry and immutable image-tag/digest convention.
- Resource requests/limits and replica/availability targets.
- External secret store, External Secrets Operator integration and workload identity.
- Ingress/TLS, network policy and public API Gateway boundary.
- Rollout and rollback strategy.

Do not copy the local overlay into production or deploy its `:ci` image tags. Track approvals in [OPEN-DECISIONS.md](../../../docs/architecture/OPEN-DECISIONS.md) and complete the Phase 8 release checklist before production.
