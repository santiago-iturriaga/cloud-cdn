################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CC_SRCS += \
../distributions.cc \
../main.cc \
../request.cc \
../stack.cc \
../stream.cc 

CC_DEPS += \
./distributions.d \
./main.d \
./request.d \
./stack.d \
./stream.d 

OBJS += \
./distributions.o \
./main.o \
./request.o \
./stack.o \
./stream.o 


# Each subdirectory must supply rules for building sources it contributes
%.o: ../%.cc
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


