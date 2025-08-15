import pandas as pd
import matplotlib.pyplot as plt
import os


def plot_bus_analysis(csv_file):
    df = pd.read_csv(csv_file)
    df['Time_Hours'] = df['TimeStep'] / 60.0
    df['SoC_Percent'] = df['SoC'] * 100

    for bus_id in df['BusID'].unique():
        bus_data = df[df['BusID'] == bus_id].copy()
        route_type = bus_data['RouteType'].iloc[0]

        # charging/regen data for indicators
        charging_times = bus_data[bus_data['IsCharging'] == True]['Time_Hours']
        regen_times = bus_data[bus_data['IsRegenerating'] == True]['Time_Hours']

        os.makedirs(f"plots/{bus_id}", exist_ok=True)

        # PLOT 1: SOC and SOH plot
        fig1, ax1 = plt.subplots(figsize=(12, 6))
        ax1.plot(bus_data['Time_Hours'], bus_data['SoC_Percent'],
                 'b-', linewidth=2, label='SoC (%)')
        ax1_twin = ax1.twinx()
        ax1_twin.plot(bus_data['Time_Hours'], bus_data['SoH'],
                      'r-', linewidth=2, label='SOH (%)')

        # charging indicators
        if len(charging_times) > 0:
            ax1.scatter(charging_times, [95]*len(charging_times),
                        color='green', s=20, label='Charging', zorder=5)
        if len(regen_times) > 0:
            ax1.scatter(regen_times, [90]*len(regen_times),
                        color='blue', s=20, label='Regen', zorder=5)

        ax1.set_ylabel('SoC (%)', color='b')
        ax1_twin.set_ylabel('SOH (%)', color='r')
        ax1.set_xlabel('Time (Hours)')
        ax1.set_title(f'{bus_id} ({route_type}) - SOC & Battery Degradation (SOH)')
        ax1.legend(loc='upper left')
        ax1_twin.legend(loc='upper right')
        ax1.grid(True, alpha=0.3)

        plt.tight_layout()
        plt.savefig(f"plots/{bus_id}/01_SOC_SOH.png", dpi=200, bbox_inches='tight')
        plt.close()

        # PLOT 2: temperature dependency plot
        fig2, ax2 = plt.subplots(figsize=(12, 6))
        ax2.plot(bus_data['Time_Hours'], bus_data['Temperature'],
                 'orange', linewidth=2, label='Temperature')
        ax2_twin = ax2.twinx()
        ax2_twin.plot(bus_data['Time_Hours'], bus_data['SoH'],
                      'purple', linewidth=2, label='SOH')

        # charging indicators
        if len(charging_times) > 0:
            ax2.scatter(charging_times, bus_data[bus_data['IsCharging'] == True]['Temperature'],
                        color='green', s=20, label='Charging', zorder=5)
        if len(regen_times) > 0:
            ax2.scatter(regen_times, bus_data[bus_data['IsRegenerating'] == True]['Temperature'],
                        color='blue', s=20, label='Regen', zorder=5)

        ax2.set_ylabel('Temperature (°C)', color='orange')
        ax2_twin.set_ylabel('SOH (%)', color='purple')
        ax2.set_xlabel('Time (Hours)')
        ax2.set_title(f'{bus_id} ({route_type}) - Temperature vs SOH (Aging Effect)')
        ax2.legend(loc='upper left')
        ax2_twin.legend(loc='upper right')
        ax2.grid(True, alpha=0.3)

        plt.tight_layout()
        plt.savefig(f"plots/{bus_id}/02_Temperature_SOH.png", dpi=200, bbox_inches='tight')
        plt.close()

        # PLOT 3: DOD plots
        fig3, ax3 = plt.subplots(figsize=(12, 6))
        ax3.plot(bus_data['Time_Hours'], bus_data['AvgDoD'],
                 'brown', linewidth=2, label='Avg DoD (%)')
        ax3_twin = ax3.twinx()
        ax3_twin.plot(bus_data['Time_Hours'], bus_data['SoH'],
                      'red', linewidth=2, label='SOH (%)')

        # charging indicators
        if len(charging_times) > 0:
            ax3.scatter(charging_times, bus_data[bus_data['IsCharging'] == True]['AvgDoD'],
                        color='green', s=20, label='Charging', zorder=5)
        if len(regen_times) > 0:
            ax3.scatter(regen_times, bus_data[bus_data['IsRegenerating'] == True]['AvgDoD'],
                        color='blue', s=20, label='Regen', zorder=5)

        ax3.set_ylabel('Average DoD (%)', color='brown')
        ax3_twin.set_ylabel('SOH (%)', color='red')
        ax3.set_xlabel('Time (Hours)')
        ax3.set_title(f'{bus_id} ({route_type}) - Depth of Discharge vs SOH')
        ax3.legend(loc='upper left')
        ax3_twin.legend(loc='upper right')
        ax3.grid(True, alpha=0.3)

        plt.tight_layout()
        plt.savefig(f"plots/{bus_id}/03_DOD_SOH.png", dpi=200, bbox_inches='tight')
        plt.close()

        # PLOT 4: current & voltage plots
        fig4, ax4 = plt.subplots(figsize=(12, 6))
        ax4.plot(bus_data['Time_Hours'], bus_data['Current'],
                 'green', linewidth=2, label='Current (A)')
        ax4_twin = ax4.twinx()
        ax4_twin.plot(bus_data['Time_Hours'], bus_data['Voltage'],
                      'red', linewidth=2, label='Voltage (V)')

        # charging/regen indicators with markers
        if len(charging_times) > 0:
            ax4.scatter(charging_times, bus_data[bus_data['IsCharging'] == True]['Current'],
                        color='lime', s=30, marker='o', label='Charging', zorder=5)
        if len(regen_times) > 0:
            ax4.scatter(regen_times, bus_data[bus_data['IsRegenerating'] == True]['Current'],
                        color='cyan', s=30, marker='s', label='Regen', zorder=5)

        ax4.axhline(y=0, color='black', linestyle='--', alpha=0.5)
        ax4.set_ylabel('Current (A)', color='green')
        ax4_twin.set_ylabel('Voltage (V)', color='red')
        ax4.set_xlabel('Time (Hours)')
        ax4.set_title(f'{bus_id} ({route_type}) - Current & Voltage (+ = Discharge, - = Charge)')
        ax4.legend(loc='upper left')
        ax4_twin.legend(loc='upper right')
        ax4.grid(True, alpha=0.3)

        plt.tight_layout()
        plt.savefig(f"plots/{bus_id}/04_Current_Voltage.png", dpi=200, bbox_inches='tight')
        plt.close()

        # PLOT 5: Cycling Data (Cycles, Ah Throughput, Calendar Age)
        fig5, ax5 = plt.subplots(figsize=(12, 6))
        ax5.plot(bus_data['Time_Hours'], bus_data['CycleCount'],
                 'purple', linewidth=2, label='Cycle Count')
        ax5_twin = ax5.twinx()
        ax5_twin.plot(bus_data['Time_Hours'], bus_data['AhThroughput'],
                      'orange', linewidth=2, label='Ah Throughput')

        # charging indicators
        if len(charging_times) > 0:
            ax5.scatter(charging_times, bus_data[bus_data['IsCharging'] == True]['CycleCount'],
                        color='green', s=20, label='Charging', zorder=5)
        if len(regen_times) > 0:
            ax5.scatter(regen_times, bus_data[bus_data['IsRegenerating'] == True]['CycleCount'],
                        color='blue', s=20, label='Regen', zorder=5)

        ax5.set_ylabel('Cycle Count', color='purple')
        ax5_twin.set_ylabel('Ah Throughput', color='orange')
        ax5.set_xlabel('Time (Hours)')
        ax5.set_title(f'{bus_id} ({route_type}) - Cycling Data: Cycles & Throughput')
        ax5.legend(loc='upper left')
        ax5_twin.legend(loc='upper right')
        ax5.grid(True, alpha=0.3)

        plt.tight_layout()
        plt.savefig(f"plots/{bus_id}/05_Cycling_Data.png", dpi=200, bbox_inches='tight')
        plt.close()

        # PLOT 6: operating Conditions (Speed, Passengers, Calendar Age)
        fig6, ax6 = plt.subplots(figsize=(12, 6))
        ax6.plot(bus_data['Time_Hours'], bus_data['Speed'],
                 'blue', linewidth=2, label='Speed (km/h)')
        ax6_twin = ax6.twinx()
        ax6_twin.plot(bus_data['Time_Hours'], bus_data['Passengers'],
                      'red', linewidth=1, label='Passengers')

        # charging indicators
        if len(charging_times) > 0:
            ax6.scatter(charging_times, bus_data[bus_data['IsCharging'] == True]['Speed'],
                        color='green', s=30, marker='o', label='Charging', zorder=5)
        if len(regen_times) > 0:
            ax6.scatter(regen_times, bus_data[bus_data['IsRegenerating'] == True]['Speed'],
                        color='blue', s=30, marker='s', label='Regen', zorder=5)

        ax6.set_ylabel('Speed (km/h)', color='blue')
        ax6_twin.set_ylabel('Passengers', color='red')
        ax6.set_xlabel('Time (Hours)')
        ax6.set_title(f'{bus_id} ({route_type}) - Operating Conditions')
        ax6.legend(loc='upper left')
        ax6_twin.legend(loc='upper right')
        ax6.grid(True, alpha=0.3)

        # calendar age and final capacity info as text
        final_data = bus_data.iloc[-1]
        info_text = f"Final Stats: Calendar Age: {final_data['CalendarAge']:.2f} days, " \
                    f"Capacity: {final_data['Capacity']:.1f} kWh, " \
                    f"SOH: {final_data['SoH']:.1f}%"
        fig6.text(0.5, 0.02, info_text, ha='center', fontsize=10,
                  bbox=dict(boxstyle="round,pad=0.3", facecolor="lightgray"))

        plt.tight_layout()
        plt.subplots_adjust(bottom=0.12)
        plt.savefig(f"plots/{bus_id}/06_Operating_Conditions.png", dpi=200, bbox_inches='tight')
        plt.close()

        print(f"Saved 6 plots for {bus_id} in plots/{bus_id}/")


def main():
    script_dir = os.path.dirname(os.path.abspath(__file__))
    csv_file = os.path.join(script_dir, "sim.csv")
    print(f"Trying to open: {csv_file}")

    print("Battery Analysis")
    print("========================")

    try:
        plot_bus_analysis(csv_file)
        print("\nAll plots generated!")

    except FileNotFoundError:
        print(f"File not found: {csv_file}")
    except Exception as e:
        print(f"Error: {e}")


if __name__ == "__main__":
    main()