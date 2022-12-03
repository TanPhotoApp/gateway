# gateway

### Reference

https://cloud.spring.io/spring-cloud-gateway/reference/html/

### Spring cloud gateway with Sleuth
By default, Spring cloud gateway with Sleuth doesn't log the trace, span. The reason is Sleuth uses the
``instrumentation-type: manual`` with reactor stack (used by spring cloud gateway)
 
Change ``instrumentation-type`` will enable trace, span
```yaml
spring:
  sleuth:
    reactor:
      instrumentation-type: decorate_queues
```