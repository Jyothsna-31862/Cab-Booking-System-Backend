# Manual Testing Guide for Circuit Breaker Implementation

## 🎯 Prerequisites

### 1. Required Services Running:
```bash
# Start services in this order:
1. Eureka Server (port 8761)
2. Config Server (port 8888) 
3. API Gateway (port 8088)
4. At least 2-3 microservices for testing (e.g., authservice, userservice, driverservice)
```

### 2. Tools You'll Need:
- **curl** (command line) OR **Postman** (GUI)
- **Browser** for dashboard access
- **Terminal/Command Prompt** for multiple windows

## 🚀 STEP 1: Basic Connectivity Test

### 1.1 Test API Gateway is Running
```bash
curl http://localhost:8088/actuator/health
```
**Expected Response:**
```json
{
  "status": "UP",
  "components": {
    "circuitBreakers": {
      "status": "UP"
    }
  }
}
```

### 1.2 Check Eureka Registration
Open browser: `http://localhost:8761`
**Verify:** You see API-GATEWAY and your microservices registered

### 1.3 Test Gateway Routes
```bash
# Test basic gateway health
curl http://localhost:8088/api/test/health
```

## 📊 STEP 2: Test Circuit Breaker Status Monitoring

### 2.1 Check All Circuit Breaker States
```bash
curl http://localhost:8088/api/circuit-breaker/status
```
**Expected:** JSON showing all services with `"state": "CLOSED"` initially

### 2.2 Check Individual Service Status
```bash
curl http://localhost:8088/api/circuit-breaker/status/auth-service
curl http://localhost:8088/api/circuit-breaker/status/user-service
```

### 2.3 Access Circuit Breaker Dashboard
Open browser: `http://localhost:8088/dashboard/circuit-breakers`
**Expected:** Web dashboard showing all circuit breaker states

## ⚡ STEP 3: Test Normal Flow (All Services UP)

### 3.1 Test Each Service Route
```bash
# Auth Service (enhanced configuration)
curl -X GET http://localhost:8088/api/auth/validate \
  -H "Authorization: Bearer test-token"

# User Service
curl http://localhost:8088/api/users/123

# Driver Service  
curl http://localhost:8088/api/drivers/456

# Other services
curl http://localhost:8088/api/rides/789
curl http://localhost:8088/api/payments/101
curl http://localhost:8088/api/ratings/202
curl http://localhost:8088/api/locations/303
```

**Expected Behavior:**
- Requests route to actual microservices
- You get real responses (or connection errors if services aren't running)
- Circuit breakers remain CLOSED

### 3.2 Monitor Circuit Breaker Metrics
```bash
curl http://localhost:8088/api/circuit-breaker/status
```
**Check:** `numberOfSuccessfulCalls` should increase for working services

## 🔥 STEP 4: Test Circuit Breaker Failure Detection

### 4.1 Stop a Microservice
**Stop one service** (e.g., userservice) and keep API Gateway running

### 4.2 Generate Failures to Trigger Circuit Breaker
```bash
# Make multiple requests to the stopped service
for i in {1..10}; do
  echo "Request $i:"
  curl http://localhost:8088/api/users/123
  echo -e "\n---"
  sleep 1
done
```

**Expected Behavior:**
- First 5 requests: Connection errors but requests still attempt to reach service
- After 5 failures: Circuit breaker opens
- Subsequent requests: Fast fallback responses (HTTP 503)

### 4.3 Verify Circuit Breaker Opened
```bash
curl http://localhost:8088/api/circuit-breaker/status/user-service
```
**Expected Response:**
```json
{
  "name": "user-service",
  "state": "OPEN",
  "failureRate": 100.0,
  "numberOfFailedCalls": 10,
  "numberOfSuccessfulCalls": 0
}
```

### 4.4 Test Fallback Response
```bash
curl http://localhost:8088/api/users/123
```
**Expected Fallback Response:**
```json
{
  "error": "SERVICE_UNAVAILABLE", 
  "message": "User Service is temporarily unavailable due to circuit breaker being open",
  "timestamp": "2025-09-25T...",
  "status": 503
}
```

## 🔄 STEP 5: Test Circuit Breaker Recovery

### 5.1 Restart the Stopped Service
Start the userservice again

### 5.2 Wait for Circuit Breaker Transition
Wait 10 seconds (waitDurationInOpenState), then check status:
```bash
curl http://localhost:8088/api/circuit-breaker/status/user-service
```
**Expected:** `"state": "HALF_OPEN"` (circuit breaker testing recovery)

### 5.3 Test Successful Recovery
Make successful requests:
```bash
# Make 3-5 successful requests
for i in {1..5}; do
  echo "Recovery test $i:"
  curl http://localhost:8088/api/users/123
  sleep 2
done
```

### 5.4 Verify Circuit Breaker Closed
```bash
curl http://localhost:8088/api/circuit-breaker/status/user-service
```
**Expected:** `"state": "CLOSED"` (circuit breaker recovered)

## 🔐 STEP 6: Test Auth Service Enhanced Configuration

### 6.1 Stop Auth Service
Stop the authservice

### 6.2 Test Auth Service Circuit Breaker
```bash
# Auth service needs 8 failures (vs 5 for others) due to enhanced config
for i in {1..12}; do
  echo "Auth test $i:"
  curl http://localhost:8088/api/auth/validate \
    -H "Authorization: Bearer test-token"
  sleep 1
done
```

### 6.3 Verify Enhanced Settings
```bash
curl http://localhost:8088/api/circuit-breaker/status/auth-service
```
**Expected Differences:**
- Takes more failures to open (8 vs 5)
- Longer wait time (15s vs 10s)

## 🎛️ STEP 7: Test Manual Circuit Breaker Control

### 7.1 Manual State Transitions
```bash
# Force open a circuit breaker
curl -X POST http://localhost:8088/api/circuit-breaker/transition/user-service/open

# Check it's open
curl http://localhost:8088/api/circuit-breaker/status/user-service

# Force close
curl -X POST http://localhost:8088/api/circuit-breaker/transition/user-service/close

# Reset circuit breaker
curl -X POST http://localhost:8088/api/circuit-breaker/reset/user-service
```

## 📈 STEP 8: Load Testing

### 8.1 Generate High Load
```bash
# Simulate multiple concurrent requests
for i in {1..20}; do
  curl http://localhost:8088/api/users/$i &
done
wait
```

### 8.2 Monitor During Load
```bash
# Watch circuit breaker metrics in real-time
watch -n 2 "curl -s http://localhost:8088/api/circuit-breaker/status/user-service"
```

## 🕵️ STEP 9: Monitoring and Logs

### 9.1 Watch Application Logs
Look for these log messages:
```
[CIRCUIT BREAKER] User Service is unavailable - Circuit breaker is OPEN
[CIRCUIT BREAKER] Auth Service is unavailable - Circuit breaker is OPEN
```

### 9.2 Actuator Endpoints
```bash
# Circuit breaker health
curl http://localhost:8088/actuator/health/circuitBreakers

# All health info
curl http://localhost:8088/actuator/health

# Metrics
curl http://localhost:8088/actuator/metrics
```

## ✅ STEP 10: Verification Checklist

### ✅ **Normal Operations Work:**
- [ ] All services route correctly when UP
- [ ] Circuit breakers show CLOSED state
- [ ] Success metrics increase
- [ ] Dashboard shows green status

### ✅ **Failure Detection Works:**
- [ ] Circuit breakers open after configured failures
- [ ] Fallback responses returned (HTTP 503)
- [ ] Failure metrics increase
- [ ] Dashboard shows red status

### ✅ **Recovery Works:**
- [ ] Circuit breakers transition OPEN → HALF_OPEN → CLOSED
- [ ] Successful requests resume after service restart
- [ ] Metrics reset properly

### ✅ **Auth Service Enhanced Settings:**
- [ ] Takes 8 failures to open (vs 5 for others)
- [ ] 60% failure threshold (vs 50%)
- [ ] 15-second wait time (vs 10s)

### ✅ **Management Works:**
- [ ] Manual state transitions work
- [ ] Reset functionality works
- [ ] Monitoring endpoints respond

## 🚨 Troubleshooting Common Issues

### Issue: Circuit Breakers Don't Open
**Check:**
- Are services actually down?
- Are you making enough requests (minimum 5-8)?
- Check YAML configuration syntax

### Issue: Fallback Not Working
**Check:**
- FallbackController endpoints exist
- Route configuration has correct fallbackUri
- No conflicting @FeignClient annotations

### Issue: Services Not Routing
**Check:**
- Eureka registration
- Port numbers
- Path predicates in YAML
- Service naming consistency

## 📝 Test Results Template

Document your test results:

```
Date: ___________
Tester: ___________

✅ Basic Connectivity: PASS/FAIL
✅ Normal Routing: PASS/FAIL  
✅ Circuit Breaker Opening: PASS/FAIL
✅ Fallback Responses: PASS/FAIL
✅ Circuit Breaker Recovery: PASS/FAIL
✅ Auth Enhanced Settings: PASS/FAIL
✅ Manual Control: PASS/FAIL

Notes:
_________________________________
```

## 🎯 Quick Test Commands Summary

```bash
# 1. Health check
curl http://localhost:8088/actuator/health

# 2. Check all circuit breaker states
curl http://localhost:8088/api/circuit-breaker/status

# 3. Test a service route
curl http://localhost:8088/api/users/123

# 4. Generate failures (with service stopped)
for i in {1..10}; do curl http://localhost:8088/api/users/123; sleep 1; done

# 5. Check if circuit breaker opened
curl http://localhost:8088/api/circuit-breaker/status/user-service
```

**Follow this guide step by step to thoroughly test your circuit breaker implementation!**
