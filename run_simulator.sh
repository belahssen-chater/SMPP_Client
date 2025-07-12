#!/bin/bash

# Function to display the menu
display_menu() {
    echo "========================================="
    echo "      SMPP SMS Simulator Runner         "
    echo "========================================="
    echo "This script runs the SmppSMSSimulator JAR file with the following inputs:"
    echo "1. SMPP Gateway Host Address"
    echo "2. Source Address (Short Code)"
    echo "3. Number of MSISDNs"
    echo "4. SMS per Second per MSISDN"
    echo "5. Path to MSISDNs Configuration File"
    echo "6. Test Duration (in seconds)"
    echo "7. SMS per Socket"
    echo "========================================="
    echo "Optional Parameters:"
    echo "8. SMPP session System ID (default: bmw.e2e)"
    echo "9. SMPP session System Type (default: bmw.e2e)"
    echo "10. SMPP session Password (default: 51205AVT)"
    echo "11. SMPP session Port (default: 9999)"
    echo "12. SMS Message Content"
    echo "========================================="
}

# Prompt the user for input
read_inputs() {
    read -p "Enter SMPP Gateway Host Address: " host
    read -p "Enter Source Address (Short Code): " srcAddr

    while true; do
        read -p "Enter Number of MSISDNs: " numberOfMsisdn
        if [[ $numberOfMsisdn =~ ^[0-9]+$ ]]; then
            break
        else
            echo "Please enter a valid number."
        fi
    done

    while true; do
        read -p "Enter SMS per Second per MSISDN: " smsPerSecondPerMsisdn
        if [[ $smsPerSecondPerMsisdn =~ ^[0-9]+$ ]]; then
            break
        else
            echo "Please enter a valid number."
        fi
    done

   while true; do
    read -p "Enter Path to MSISDNs Configuration File: " path_to_msisdns
    if [[ -f $path_to_msisdns ]]; then
      break
    else
        echo "Warning: File '$path_to_msisdns' does not exist."
    fi
  done

    while true; do
        read -p "Enter Test Duration (in seconds): " test_duration_sec
        if [[ $test_duration_sec =~ ^[0-9]+$ ]]; then
            break
        else
            echo "Please enter a valid number."
        fi
    done

    while true; do
        read -p "Enter SMS per Socket [1]: " smsPerSocket
        if [[ -z "$smsPerSocket" ]]; then
            smsPerSocket=1
            break
        elif [[ $smsPerSocket =~ ^[0-9]+$ ]] && [[ $smsPerSocket -gt 0 ]]; then
            break
        else
            echo "Please enter a valid positive number."
        fi
    done


    # Optional parameters
    echo -e "\n Optional Parameters (press Enter to use defaults):"
    read -p "Enter System ID [bmw.e2e]: " systemId
    read -p "Enter System Type [bmw.e2e]: " systemType
    read -p "Enter Password [51205AVT]: " password

while true; do
        read -p "Enter SMPP session Port [9999]: " port
        if [[ -z "$port" ]] || [[ $port =~ ^[0-9]+$ ]]; then
            break
        else
            echo "Please enter a valid number or press Enter for default."
        fi
    done

    read -p "Enter Message Content [Hello World €$£]: " message
}
# Run the Java JAR file
run_simulator() {
    echo "Running smpp-sms-simulator-1.0-SNAPSHOT.jar with the following parameters:"
    echo "Host: $host"
    echo "Source Address: $srcAddr"
    echo "Number of MSISDNs: $numberOfMsisdn"
    echo "SMS per Second per MSISDN: $smsPerSecondPerMsisdn"
    echo "Path to MSISDNs: $path_to_msisdns"
    echo "Test Duration: $test_duration_sec seconds"
    echo "SMS per Socket: $smsPerSocket"

cmd="$JAVA_HOME/bin/java -jar smpp-sms-simulator-1.0-SNAPSHOT.jar \
        \"$host\" \
        \"$srcAddr\" \
        \"$numberOfMsisdn\" \
        \"$smsPerSecondPerMsisdn\" \
        \"$path_to_msisdns\" \
        \"$test_duration_sec\" \
        \"$smsPerSocket\""

    # Add optional parameters if provided
    [[ ! -z "$systemId" ]] && cmd="$cmd \"systemId=$systemId\""
    [[ ! -z "$systemType" ]] && cmd="$cmd \"systemType=$systemType\""
    [[ ! -z "$password" ]] && cmd="$cmd \"password=$password\""
    [[ ! -z "$port" ]] && cmd="$cmd \"port=$port\""
    [[ ! -z "$message" ]] && cmd="$cmd \"message=$message\""

#    echo "Final command to execute: "
#    echo "$cmd"

    eval $cmd

}

# Main script
clear
display_menu
read_inputs
run_simulator
